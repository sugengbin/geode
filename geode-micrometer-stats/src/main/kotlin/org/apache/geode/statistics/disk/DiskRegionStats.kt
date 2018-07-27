package org.apache.geode.statistics.disk

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter

class DiskRegionStats(diskRegionName: String) : MicrometerMeterGroup("DiskRegionStats-$diskRegionName") {
    override fun initializeStaticMeters() {
        registerMeter(diskStoreWriteMeter)
        registerMeter(diskStoreWriteTimer)
        registerMeter(diskStoreWriteBytesMeter)
        registerMeter(diskStoreWriteInProgressMeter)
        registerMeter(diskStoreReadMeter)
        registerMeter(diskStoreReadTimer)
        registerMeter(diskStoreReadBytesMeter)
        registerMeter(diskStoreRemoveMeter)
        registerMeter(diskStoreRemoveTimer)
        registerMeter(diskStoreEntriesOnDiskOnlyMeter)
        registerMeter(diskStoreEntriesOnDiskOnlyBytesMeter)
        registerMeter(diskStoreEntriesInVMMeter)
        registerMeter(diskStoreRegionLocalInitializationMeter)
        registerMeter(diskStoreRegionRemoteInitializationMeter)
    }

    private val diskStoreWriteMeter = CounterStatisticMeter("diskregion.entry.write.count", "The total number of region entries that have been written to diskregion. A write is done every time an entry is created on disk or every time its value is modified on diskregion.", arrayOf("diskRegionName", diskRegionName))
    private val diskStoreWriteTimer = TimerStatisticMeter("diskregion.entry.write.time", "The total amount of time spent writing to disk", arrayOf("diskRegionName", diskRegionName), unit = "nanoseconds")
    private val diskStoreWriteBytesMeter = CounterStatisticMeter("diskregion.write.bytes", "The total number of bytes that have been written to disk", arrayOf("diskRegionName", diskRegionName), unit = "bytes")
    private val diskStoreWriteInProgressMeter = GaugeStatisticMeter("diskregion.write.inprogress.count", "current number of oplog writes that are in progress", arrayOf("diskRegionName", diskRegionName))
    private val diskStoreReadMeter = CounterStatisticMeter("diskregion.entry.read.count", "The total number of region entries that have been read from disk", arrayOf("diskRegionName", diskRegionName))
    private val diskStoreReadTimer = TimerStatisticMeter("diskregion.entry.read.time", "The total amount of time spent reading from disk", arrayOf("diskRegionName", diskRegionName), unit = "nanoseconds")
    private val diskStoreReadBytesMeter = CounterStatisticMeter("diskregion.read.bytes", "The total number of bytes that have been read from disk", arrayOf("diskRegionName", diskRegionName), unit = "bytes")
    private val diskStoreRemoveMeter = CounterStatisticMeter("diskregion.entry.remove.count", "The total number of region entries that have been removed from disk", arrayOf("diskRegionName", diskRegionName))
    private val diskStoreRemoveTimer = TimerStatisticMeter("diskregion.entry.remove.time", "The total amount of time spent removing from disk", arrayOf("diskRegionName", diskRegionName), unit = "nanoseconds")
    private val diskStoreEntriesOnDiskOnlyMeter = GaugeStatisticMeter("diskregion.entry.diskregion.count", "The current number of entries whose value is on disk and is not in memory. This is true of overflowed entries. It is also true of recovered entries that have not yet been faulted in.", arrayOf("diskRegionName", diskRegionName))
    private val diskStoreEntriesOnDiskOnlyBytesMeter = GaugeStatisticMeter("diskregion.entry.diskregion.bytes", "The current number bytes on disk and not in memory. This is true of overflowed entries. It is also true of recovered entries that have not yet been faulted in.", arrayOf("diskRegionName", diskRegionName), unit = "bytes")
    private val diskStoreEntriesInVMMeter = GaugeStatisticMeter("diskregion.entry.vm.count", "The current number of entries whose value resides in the VM. The value may also have been written to diskregion.", arrayOf("diskRegionName", diskRegionName))
    private val diskStoreRegionLocalInitializationMeter = GaugeStatisticMeter("diskregion.region.init.local.count", "The number of times that this region has been initialized solely from the local disk files (0 or 1)", arrayOf("diskRegionName", diskRegionName))
    private val diskStoreRegionRemoteInitializationMeter = GaugeStatisticMeter("diskregion.region.init.gii.count", "The number of times that this region has been initialized by doing GII from a peer (0 or 1)", arrayOf("diskRegionName", diskRegionName))


    fun incNumOverflowOnDisk(delta: Long) {
        diskStoreEntriesOnDiskOnlyMeter.increment(delta)
    }

    fun incNumEntriesInVM(delta: Long) {
        diskStoreEntriesInVMMeter.increment(delta)
    }

    fun incNumOverflowBytesOnDisk(delta: Long) {
        diskStoreEntriesOnDiskOnlyBytesMeter.increment(delta)
    }

    fun startWrite() {
        diskStoreWriteInProgressMeter.increment()
    }

    fun incWrittenBytes(bytesWritten: Long) {
        diskStoreWriteBytesMeter.increment(bytesWritten)
    }

    fun endWrite(start: Long, end: Long) {
        diskStoreWriteInProgressMeter.decrement()
        diskStoreWriteMeter.increment()
        diskStoreWriteTimer.recordValue(end - start)
    }

    fun endRead(start: Long, end: Long, bytesRead: Long) {
        diskStoreReadMeter.increment()
        diskStoreReadTimer.recordValue(end - start)
        diskStoreReadBytesMeter.increment(bytesRead)
    }

    fun endRemove(start: Long, end: Long) {
        diskStoreRemoveMeter.increment()
        diskStoreRemoveTimer.recordValue(end - start)
    }

    fun incInitializations(local: Boolean) {
        if (local) {
            diskStoreRegionLocalInitializationMeter.increment()
        } else {
            diskStoreRegionRemoteInitializationMeter.increment()
        }
    }
}