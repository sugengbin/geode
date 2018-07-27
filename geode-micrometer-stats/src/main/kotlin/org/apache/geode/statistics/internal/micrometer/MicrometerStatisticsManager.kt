package org.apache.geode.statistics.internal.micrometer

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.config.MeterFilterReply
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.apache.geode.statistics.StatisticsManager
import org.apache.geode.statistics.StatisticsMeterGroup
import java.net.InetAddress

class MicrometerStatisticsManager @JvmOverloads constructor(private val enableStats: Boolean = true,
                                                            private val serverName: String = "cacheServer" + InetAddress.getLocalHost().hostAddress,
                                                            private val meterRegistry: CompositeMeterRegistry =
                                                                    CompositeMeterRegistry(Clock.SYSTEM)) : StatisticsManager {
    private val registeredMeterGroups = mutableMapOf<String, MicrometerMeterGroup>()

    init {
        meterRegistry.add(SimpleMeterRegistry())
        meterRegistry.config().commonTags("serverName", serverName)
    }

    override fun registerMeterGroup(groupName: String, meterGroup: StatisticsMeterGroup) {
        if (meterGroup is MicrometerMeterGroup) {
            registeredMeterGroups.putIfAbsent(groupName, meterGroup)
                    ?.run { throw RuntimeException("MeterGroup: $groupName was already registered") }
            if (!enableStats) {
                meterRegistry.config().meterFilter(object : MeterFilter {
                    override fun accept(id: Meter.Id): MeterFilterReply {
                        return MeterFilterReply.DENY
                    }
                })
            }
            meterGroup.bindTo(meterRegistry)
        } else {
            //TODO: Register Non-MircometerMeterGrouops, this feature is not yet supported. Most likely never will be
        }
    }
}