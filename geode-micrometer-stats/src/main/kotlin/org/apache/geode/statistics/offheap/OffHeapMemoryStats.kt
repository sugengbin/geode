package org.apache.geode.statistics.offheap

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter
import org.apache.geode.statistics.util.NOW_NANOS

class OffHeapMemoryStats(regionName: String) : MicrometerMeterGroup("OffHeapMemoryStats-$regionName") {
    override fun initializeStaticMeters() {
        registerMeter(offheapMemoryUsedBytesMeter)
        registerMeter(offheapMemoryMaxBytesMeter)
        registerMeter(offheapMemoryDefragmentationMeter)
        registerMeter(offheapMemoryDefragmentationInProgressMeter)
        registerMeter(offheapMemoryDefragmentationTimer)
        registerMeter(offheapMemoryFragmentationMeter)
        registerMeter(offheapMemoryFragmentMeter)
        registerMeter(offheapMemoryFreeBytesMeter)
        registerMeter(offheapMemoryLargestFragmentBytesMeter)
        registerMeter(offheapMemoryObjectCountMeter)
        registerMeter(offheapMemoryReadCountMeter)
    }

    private val offheapMemoryUsedBytesMeter = GaugeStatisticMeter("offheap.memory.used.bytes", "The amount of off-heap memory, in bytes, that is being used to store data.", arrayOf("offheapRegion", regionName), unit = "bytes")
    private val offheapMemoryMaxBytesMeter = GaugeStatisticMeter("offheap.memory.max.bytes", "The maximum amount of off-heap memory, in bytes. This is the amount of memory allocated at startup and does not change.", arrayOf("offheapRegion", regionName))
    private val offheapMemoryDefragmentationMeter = CounterStatisticMeter("offheap.memory.defragmentations.count", "The total number of times off-heap memory has been defragmented.", arrayOf("offheapRegion", regionName))
    private val offheapMemoryDefragmentationInProgressMeter = GaugeStatisticMeter("offheap.memory.defragmentations.inprogress.count", "Current number of defragment operations currently in progress.", arrayOf("offheapRegion", regionName))
    private val offheapMemoryDefragmentationTimer = TimerStatisticMeter("offheap.memory.defragmentation.time", "The total time spent defragmenting off-heap memory.", arrayOf("offheapRegion", regionName), unit = "nanoseconds")
    private val offheapMemoryFragmentationMeter = GaugeStatisticMeter("offheap.memory.fragmentation.percentage", "The percentage of off-heap free memory that is fragmented.  Updated every time a defragmentation is performed.", arrayOf("offheapRegion", regionName))
    private val offheapMemoryFragmentMeter = GaugeStatisticMeter("offheap.memory.fragments.count", "The number of fragments of free off-heap memory. Updated every time a defragmentation is done.", arrayOf("offheapRegion", regionName))
    private val offheapMemoryFreeBytesMeter = GaugeStatisticMeter("offheap.memory.free.bytes", "The amount of off-heap memory, in bytes, that is not being used.", arrayOf("offheapRegion", regionName))
    private val offheapMemoryLargestFragmentBytesMeter = GaugeStatisticMeter("offheap.memory.largest.fragment.bytes", "The largest fragment of memory found by the last defragmentation of off heap memory. Updated every time a defragmentation is done.", arrayOf("offheapRegion", regionName))
    private val offheapMemoryObjectCountMeter = GaugeStatisticMeter("offheap.memory.object.count", "The number of objects stored in off-heap memory.", arrayOf("offheapRegion", regionName))
    private val offheapMemoryReadCountMeter = CounterStatisticMeter("offheap.memory.read.count", "The total number of reads of off-heap memory. Only reads of a full object increment this statistic. If only a part of the object is read this statistic is not incremented.", arrayOf("offheapRegion", regionName))

    fun incFreeMemory(value: Long) {
        offheapMemoryFreeBytesMeter.increment(value)
    }

    fun incMaxMemory(value: Long) {
        offheapMemoryMaxBytesMeter.increment(value)
    }

    fun incUsedMemory(value: Long) {
        offheapMemoryUsedBytesMeter.increment(value)
    }

    fun incObjects(value: Int) {
        offheapMemoryObjectCountMeter.increment(value)
    }

    fun incReads() {
        offheapMemoryReadCountMeter.increment()
    }

    private fun incDefragmentations() {
        offheapMemoryDefragmentationMeter.increment()
    }

    fun setFragments(value: Long) {
        offheapMemoryFragmentMeter.setValue(value)
    }

    fun setLargestFragment(value: Int) {
        offheapMemoryLargestFragmentBytesMeter.setValue(value)
    }

    fun startDefragmentation(): Long {
        offheapMemoryDefragmentationInProgressMeter.increment()
        return NOW_NANOS
    }

    fun endDefragmentation(start: Long) {
        incDefragmentations()
        offheapMemoryDefragmentationInProgressMeter.decrement()
        offheapMemoryDefragmentationTimer.recordValue(NOW_NANOS - start)
    }

    fun setFragmentation(value: Int) {
        offheapMemoryFragmentationMeter.setValue(value)
    }
}