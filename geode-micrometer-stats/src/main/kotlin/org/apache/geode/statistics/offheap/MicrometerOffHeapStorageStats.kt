package org.apache.geode.statistics.offheap

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter
import java.util.concurrent.TimeUnit

class MicrometerOffHeapStorageStats : MicrometerMeterGroup("OffHeapMemoryStats"), OffHeapStorageStats {
    companion object {
        private const val usedMemoryDesc = "The amount of off-heap memory, in bytes, that is being used to store data."
        private const val defragmentationTimeDesc = "The total time spent defragmenting off-heap memory."
        private const val fragmentationDesc = "The percentage of off-heap free memory that is fragmented.  Updated every time a defragmentation is performed."
        private const val fragmentsDesc = "The number of fragments of free off-heap memory. Updated every time a defragmentation is done."
        private const val freeMemoryDesc = "The amount of off-heap memory, in bytes, that is not being used."
        private const val largestFragmentDesc = "The largest fragment of memory found by the last defragmentation of off heap memory. Updated every time a defragmentation is done."
        private const val objectsDesc = "The number of objects stored in off-heap memory."
        private const val readsDesc = "The total number of reads of off-heap memory. Only reads of a full object increment this statistic. If only a part of the object is read this statistic is not incremented."
        private const val maxMemoryDesc = "The maximum amount of off-heap memory, in bytes. This is the amount of memory allocated at startup and does not change."
    }

    private val freeMemoryMeter = GaugeStatisticMeter("offheap.free.memory", freeMemoryDesc, unit="bytes")
    private val maxMemoryMeter = GaugeStatisticMeter("offheap.max.memory", maxMemoryDesc, unit="bytes")
    private val usedMemoryMeter = GaugeStatisticMeter("offheap.used.memory", usedMemoryDesc, unit="bytes")
    private val objectReadMeter = CounterStatisticMeter("offheap.object.reads", readsDesc)
    private val objectsStoredMeter = GaugeStatisticMeter("offheap.object.stored", objectsDesc)
    private val fragmentationPercentageMeter = GaugeStatisticMeter("offheap.fragments.percentage", fragmentationDesc, unit="percentage")
    private val fragmentCountMeter = GaugeStatisticMeter("offheap.fragments.count", fragmentsDesc)
    private val largestFragmentMeter = GaugeStatisticMeter("offheap.fragments.max", largestFragmentDesc, unit="bytes")
    private val defragmentationTimeTimer = TimerStatisticMeter("offheap.defragmentation.time", defragmentationTimeDesc, unit="nanoseconds")

    override fun initializeStaticMeters() {
        registerMeter(freeMemoryMeter)
        registerMeter(maxMemoryMeter)
        registerMeter(usedMemoryMeter)
        registerMeter(objectReadMeter)
        registerMeter(objectsStoredMeter)
        registerMeter(fragmentationPercentageMeter)
        registerMeter(fragmentCountMeter)
        registerMeter(largestFragmentMeter)
        registerMeter(defragmentationTimeTimer)
    }

    override fun incFreeMemory(value: Long) {
        freeMemoryMeter.increment(value.toDouble())
    }

    override fun incMaxMemory(value: Long) {
        maxMemoryMeter.increment(value.toDouble())
    }

    override fun incUsedMemory(value: Long) {
        usedMemoryMeter.increment(value.toDouble())
    }

    override fun incObjects(value: Int) {
        objectsStoredMeter.increment(value.toDouble())
    }

    override fun incReads() {
        objectReadMeter.increment()
    }

    override fun setFragments(value: Long) {
        fragmentCountMeter.setValue(value.toDouble())
    }

    override fun setLargestFragment(value: Int) {
        largestFragmentMeter.setValue(value.toDouble())
    }

    override fun incDefragmentationTime(value: Long, timeUnit: TimeUnit) {
        defragmentationTimeTimer.recordValue(value, timeUnit)
    }

    override fun setFragmentation(value: Int) {
        fragmentationPercentageMeter.setValue(value.toDouble())
    }
}