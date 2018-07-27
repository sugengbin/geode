package org.apache.geode.statistics.internal.micrometer

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import org.apache.geode.statistics.StatisticsMeterGroup

abstract class MicrometerMeterGroup(private val groupName: String) : StatisticsMeterGroup, MeterBinder {
    private val registeredMeters = mutableListOf<MicrometerStatisticMeter>()
    private val registeredMeterGroups = mutableListOf<MicrometerMeterGroup>()

    abstract fun initializeStaticMeters()

    override fun getMeterGroupName(): String = groupName
    override fun bindTo(registry: MeterRegistry) {
        registeredMeters.forEach { it.register(registry) }
        registeredMeterGroups.forEach { micrometerMeterGroup -> micrometerMeterGroup.registeredMeters.forEach { it.register(registry) } }
    }

    protected fun registerMeter(meter: MicrometerStatisticMeter) {
        registeredMeters.add(meter)
    }

    protected fun registerMeterGroup(meterGroup: MicrometerMeterGroup) {
        registeredMeterGroups.add(meterGroup)
    }
}