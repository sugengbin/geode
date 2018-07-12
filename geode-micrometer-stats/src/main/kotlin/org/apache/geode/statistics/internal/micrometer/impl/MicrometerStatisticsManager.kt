package org.apache.geode.statistics.internal.micrometer.impl

import com.sun.net.httpserver.HttpServer
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.config.MeterFilterReply
import io.micrometer.influx.InfluxConfig
import io.micrometer.influx.InfluxMeterRegistry
import io.micrometer.jmx.JmxConfig
import io.micrometer.jmx.JmxMeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.apache.geode.statistics.internal.micrometer.StatisticsManager
import org.apache.geode.statistics.internal.micrometer.StatisticsMeterGroup
import java.io.IOException
import java.io.OutputStream
import java.lang.management.ManagementFactory
import java.net.InetAddress
import java.net.InetSocketAddress
import java.time.Duration

object MicrometerStatisticsManager : StatisticsManager {

    //    @JvmOverloads constructor(private val enableStats: Boolean = true,
//                                                            private val serverName: String = "cacheServer" + InetAddress.getLocalHost().hostAddress,
//                                                            vararg meterRegistries: MeterRegistry,
//                                                            private val meterRegistry: CompositeMeterRegistry =
//                                                                    CompositeMeterRegistry(Clock.SYSTEM)) : StatisticsManager {
    private val registeredMeterGroups = mutableMapOf<String, MicrometerMeterGroup>()
    private val meterRegistry: CompositeMeterRegistry = createCompositeRegistry()
    private var serverName: String = "cacheServer_" + ManagementFactory.getRuntimeMXBean().name
    private var enableStats: Boolean = true

    fun registerMeterRegistries(vararg meterRegistries: MeterRegistry) {
        meterRegistries.forEach { meterRegistry.add(it) }
    }

    fun disableStatsCollection() {
        enableStats = false
    }


    init {
//        meterRegistries.forEach { meterRegistry.add(it) }
        meterRegistry.config().commonTags("serverName", serverName)
    }

    override fun registerMeterRegistry(meterRegistry: MeterRegistry) {
        this.meterRegistry.add(meterRegistry)
    }

    override fun registerMeterGroup(groupName: String, meterGroup: StatisticsMeterGroup) {
        if (meterGroup is MicrometerMeterGroup) {
            registeredMeterGroups.putIfAbsent(groupName, meterGroup)
                    ?.run { println("MeterGroup: $groupName was already registered") }
            if (!enableStats) {
                meterRegistry.config().meterFilter(object : MeterFilter {
                    override fun accept(id: Meter.Id): MeterFilterReply {
                        return MeterFilterReply.DENY
                    }
                })
            }
            meterGroup.bindTo(meterRegistry)
        } else {
            TODO("Register Non-MircometerMeterGrouops, this feature is not yet supported. Most likely never will be")
        }
    }

    fun createWithRegistries(meterRegistries: Array<out MeterRegistry>): MicrometerStatisticsManager {
        registerMeterRegistries(*meterRegistries)
        return this
    }

    private fun createCompositeRegistry(): CompositeMeterRegistry {
        val compositeMeterRegistry = CompositeMeterRegistry(Clock.SYSTEM)
        compositeMeterRegistry.add(createInfluxDB())
//        compositeMeterRegistry.add(createPrometheus())
//        compositeMeterRegistry.add(createJMX())
        return compositeMeterRegistry
    }

    private fun createJMX(): JmxMeterRegistry {
        return JmxMeterRegistry(JmxConfig { null }, Clock.SYSTEM)
    }

    private fun createInfluxDB(): InfluxMeterRegistry {
        val config = object : InfluxConfig {
            override fun step(): Duration = Duration.ofSeconds(10)
            override fun db(): String = "mydb"
            override fun get(k: String): String? = null
        }
        return InfluxMeterRegistry(config, Clock.SYSTEM)
    }

    private fun createPrometheus(): PrometheusMeterRegistry {
        val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        try {
            val port = if (System.getProperty("populateData")?.toBoolean() == true) {
                10080
            } else {
                10081
            }
            val server = HttpServer.create(InetSocketAddress(port), 0)
            server.createContext("/geode") {
                val response = prometheusRegistry.scrape()
                it.sendResponseHeaders(200, response.toByteArray().size.toLong())
                it.responseBody?.run { this.write(response.toByteArray()) }
            }
            Thread(server::start).start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return prometheusRegistry
    }
}