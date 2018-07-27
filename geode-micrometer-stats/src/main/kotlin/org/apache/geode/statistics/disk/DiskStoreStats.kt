package org.apache.geode.statistics.disk

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter
import org.apache.geode.statistics.util.NOW_NANOS

class DiskStoreStats(diskStoreName: String) : MicrometerMeterGroup("DiskStoreStats-$diskStoreName") {
    override fun initializeStaticMeters() {
        registerMeter(diskStoreWriteToDiskMeter)
        registerMeter(diskStoreWriteToDiskTimer)
        registerMeter(diskStoreWriteToDiskBytesMeter)
        registerMeter(diskStoreFlushToDiskMeter)
        registerMeter(diskStoreFlushToDiskTimer)
        registerMeter(diskStoreFlushToDiskBytesMeter)
        registerMeter(diskStoreReadFromDiskMeter)
        registerMeter(diskStoreReadFromDiskTimer)
        registerMeter(diskStoreReadFromDiskBytesMeter)
        registerMeter(diskStoreRecoveryInProgressMeter)
        registerMeter(diskStoreRecoveryTimer)
        registerMeter(diskStoreRecoveryBytesMeter)
        registerMeter(diskStoreRecoveryEntriesCreateMeter)
        registerMeter(diskStoreRecoveryEntriesUpdateMeter)
        registerMeter(diskStoreRecoveryEntriesDestroyMeter)
        registerMeter(diskStoreRecoverySkipDueToLRUMeter)
        registerMeter(diskStoreRecoverySkipMeter)
        registerMeter(diskStoreOplogRecoveryMeter)
        registerMeter(diskStoreOplogRecoveryTimer)
        registerMeter(diskStoreOplogRecoveryBytesMeter)
        registerMeter(diskStoreEntryRemovedMeter)
        registerMeter(diskStoreEntryRemovedTimer)
        registerMeter(diskStoreQueueSizeMeter)
        registerMeter(diskStoreCompactInsertMeter)
        registerMeter(diskStoreCompactInsertTimer)
        registerMeter(diskStoreCompactUpdateMeter)
        registerMeter(diskStoreCompactUpdateTimer)
        registerMeter(diskStoreCompactDestroyMeter)
        registerMeter(diskStoreCompactDestroyTimer)
        registerMeter(diskStoreCompactInProgressMeter)
        registerMeter(diskStoreWriteInProgressMeter)
        registerMeter(diskStoreFlushesInProgressMeter)
        registerMeter(diskStoreCompactTimer)
        registerMeter(diskStoreCompactMeter)
        registerMeter(diskStoreOplogOpenMeter)
        registerMeter(diskStoreOplogCompactableMeter)
        registerMeter(diskStoreOplogInactiveMeter)
        registerMeter(diskStoreOplogReadMeter)
        registerMeter(diskStoreOplogSeekMeter)
        registerMeter(diskStoreRegionsNotRecoveredMeter)
        registerMeter(diskStoreBackupInProgressMeter)
        registerMeter(diskStoreBackupCompletedMeter)
    }

    val diskStoreWriteToDiskMeter = GaugeStatisticMeter("disk.entries.write.count", "The total number of region entries that have been written to disk. A write is done every time an entry is created on disk or every time its value is modified on disk.", arrayOf("diskStoreName", diskStoreName))
    val diskStoreWriteToDiskTimer = TimerStatisticMeter("disk.entries.write.time", "The total amount of time spent writing to disk", arrayOf("diskStoreName", diskStoreName), unit = "nanoseconds")
    val diskStoreWriteToDiskBytesMeter = GaugeStatisticMeter("disk.entries.write.bytes", "The total number of bytes that have been written to disk", arrayOf("diskStoreName", diskStoreName), unit = "bytes")
    val diskStoreFlushToDiskMeter = GaugeStatisticMeter("disk.entries.flush.count", "The total number of times the an entry has been flushed from the async queue.", arrayOf("diskStoreName", diskStoreName))
    val diskStoreFlushToDiskTimer = TimerStatisticMeter("disk.entries.flush.time", "The total amount of time spent doing an async queue flush.", arrayOf("diskStoreName", diskStoreName), unit = "nanoseconds")
    val diskStoreFlushToDiskBytesMeter = GaugeStatisticMeter("disk.entries.flush.bytes", "The total number of bytes written to disk by async queue flushes.", arrayOf("diskStoreName", diskStoreName), unit = "bytes")
    val diskStoreReadFromDiskMeter = GaugeStatisticMeter("disk.entries.read.count", "The total number of region entries that have been read from disk", arrayOf("diskStoreName", diskStoreName))
    val diskStoreReadFromDiskTimer = TimerStatisticMeter("disk.entries.read.time", "The total amount of time spent reading from disk", arrayOf("diskStoreName", diskStoreName), unit = "nanoseconds")
    val diskStoreReadFromDiskBytesMeter = GaugeStatisticMeter("disk.entries.read.bytes", "The total number of bytes that have been read from disk", arrayOf("diskStoreName", diskStoreName), unit = "bytes")
    val diskStoreRecoveryInProgressMeter = GaugeStatisticMeter("disk.recovery.inprogress", "current number of persistent regions being recovered from disk", arrayOf("diskStoreName", diskStoreName))
    val diskStoreRecoveryTimer = TimerStatisticMeter("disk.recovery.time", "The total amount of time spent doing a recovery", arrayOf("diskStoreName", diskStoreName), unit = "nanoseconds")
    val diskStoreRecoveryBytesMeter = GaugeStatisticMeter("disk.recovery.bytes", "The total number of bytes that have been read from disk during a recovery", arrayOf("diskStoreName", diskStoreName), unit = "bytes")
    val diskStoreRecoveryEntriesCreateMeter = GaugeStatisticMeter("disk.recovery.entry.operation.count", "The total number of entry create records processed while recovering oplog data.", arrayOf("diskStoreName", diskStoreName, "operationType", "create"))
    val diskStoreRecoveryEntriesUpdateMeter = GaugeStatisticMeter("disk.recovery.entry.operation.count", "The total number of entry update records processed while recovering oplog data.", arrayOf("diskStoreName", diskStoreName, "operationType", "update"))
    val diskStoreRecoveryEntriesDestroyMeter = GaugeStatisticMeter("disk.recovery.entry.operation.count", "The total number of entry destroy records processed while recovering oplog data.", arrayOf("diskStoreName", diskStoreName, "operationType", "destroy"))
    val diskStoreRecoverySkipDueToLRUMeter = GaugeStatisticMeter("disk.recovery.entry.skip.count", "The total number of entry values that did not need to be recovered due to the LRU.", arrayOf("diskStoreName", diskStoreName, "reason", "lru"))
    val diskStoreRecoverySkipMeter = GaugeStatisticMeter("disk.recovery.entry.skip.count", "The total number of oplog records skipped during recovery.", arrayOf("diskStoreName", diskStoreName))
    val diskStoreOplogRecoveryMeter = GaugeStatisticMeter("disk.oplog.recovery.count", "The total number of oplogs recovered", arrayOf("diskStoreName", diskStoreName))
    val diskStoreOplogRecoveryTimer = TimerStatisticMeter("disk.oplog.recovery.time", "The total amount of time spent doing an oplog recovery", arrayOf("diskStoreName", diskStoreName), unit = "nanoseconds")
    val diskStoreOplogRecoveryBytesMeter = GaugeStatisticMeter("disk.oplog.recovery.bytes", "The total number of bytes that have been read from oplogs during a recovery", arrayOf("diskStoreName", diskStoreName), unit = "bytes")
    val diskStoreEntryRemovedMeter = GaugeStatisticMeter("disk.entries.remove.count", "The total number of region entries that have been removed from disk", arrayOf("diskStoreName", diskStoreName))
    val diskStoreEntryRemovedTimer = TimerStatisticMeter("disk.entries.remove.time", "The total amount of time spent removing from disk", arrayOf("diskStoreName", diskStoreName), unit = "nanoseconds")
    val diskStoreQueueSizeMeter = GaugeStatisticMeter("disk.queue.count", "The current number of entries in the async queue waiting to be flushed to disk", arrayOf("diskStoreName", diskStoreName))
    val diskStoreCompactInsertMeter = GaugeStatisticMeter("disk.compact.operation.count", "Total number of times an oplog compact did a db insert", arrayOf("diskStoreName", diskStoreName, "operationType", "insert"))
    val diskStoreCompactInsertTimer = TimerStatisticMeter("disk.compact.operation.time", "Total amount of time, in nanoseconds, spent doing inserts during a compact", arrayOf("diskStoreName", diskStoreName, "operationType", "insert"), unit = "nanoseconds")
    val diskStoreCompactUpdateMeter = GaugeStatisticMeter("disk.compact.operation.count", "Total number of times an oplog compact did an update", arrayOf("diskStoreName", diskStoreName, "operationType", "update"))
    val diskStoreCompactUpdateTimer = TimerStatisticMeter("disk.compact.operation.time", "Total amount of time, in nanoseconds, spent doing updates during a compact", arrayOf("diskStoreName", diskStoreName, "operationType", "update"), unit = "nanoseconds")
    val diskStoreCompactDestroyMeter = CounterStatisticMeter("disk.compact.operation.count", "Total number of times an oplog compact did a delete", arrayOf("diskStoreName", diskStoreName, "operationType", "destroy"))
    val diskStoreCompactDestroyTimer = TimerStatisticMeter("disk.compact.operation.time", "Total amount of time, in nanoseconds, spent doing deletes during a compact", arrayOf("diskStoreName", diskStoreName, "operationType", "destroy"), unit = "nanoseconds")
    val diskStoreCompactInProgressMeter = GaugeStatisticMeter("disk.compact.inprogress.count", "current number of oplog compacts that are in progress", arrayOf("diskStoreName", diskStoreName))
    val diskStoreWriteInProgressMeter = GaugeStatisticMeter("disk.oplog.write.inprogress.count", "current number of oplog writes that are in progress", arrayOf("diskStoreName", diskStoreName))
    val diskStoreFlushesInProgressMeter = GaugeStatisticMeter("disk.oplog.flush.inprogress.count", "current number of oplog flushes that are in progress", arrayOf("diskStoreName", diskStoreName))
    val diskStoreCompactTimer = TimerStatisticMeter("disk.compact.time", "Total amount of time, in nanoseconds, spent compacting oplogs", arrayOf("diskStoreName", diskStoreName), unit = "nanoseconds")
    val diskStoreCompactMeter = CounterStatisticMeter("disk.compact.count", "Total number of completed oplog compacts", arrayOf("diskStoreName", diskStoreName), "compacts")
    val diskStoreOplogOpenMeter = GaugeStatisticMeter("disk.oplog.open.count", "Current number of oplogs this disk store has open", arrayOf("diskStoreName", diskStoreName))
    val diskStoreOplogCompactableMeter = GaugeStatisticMeter("disk.oplog.compactable.count", "Current number of oplogs ready to be compacted", arrayOf("diskStoreName", diskStoreName))
    val diskStoreOplogInactiveMeter = GaugeStatisticMeter("disk.oplog.notactive.count", "Current number of oplogs that are no longer being written but are not ready ready to compact", arrayOf("diskStoreName", diskStoreName))
    val diskStoreOplogReadMeter = CounterStatisticMeter("disk.oplog.read.count", "Total number of oplog reads", arrayOf("diskStoreName", diskStoreName))
    val diskStoreOplogSeekMeter = CounterStatisticMeter("disk.oplog.seek.count", "Total number of oplog seeks", arrayOf("diskStoreName", diskStoreName))
    val diskStoreRegionsNotRecoveredMeter = GaugeStatisticMeter("disk.recovery.regions.notrecovered.count", "The current number of regions that have been recovered but have not yet been created.", arrayOf("diskStoreName", diskStoreName))
    val diskStoreBackupInProgressMeter = GaugeStatisticMeter("disk.backup.inprogress.count", "The current number of backups in progress on this disk store", arrayOf("diskStoreName", diskStoreName))
    val diskStoreBackupCompletedMeter = GaugeStatisticMeter("disk.backup.completed.count", "The number of backups of this disk store that have been taking while this VM was alive", arrayOf("diskStoreName", diskStoreName))


    fun setQueueSize(value: Int) {
        diskStoreQueueSizeMeter.setValue(value)
    }

    fun incQueueSize(delta: Int) {
        diskStoreQueueSizeMeter.increment(delta)
    }

    fun incUncreatedRecoveredRegions(delta: Int) {
        diskStoreRegionsNotRecoveredMeter.increment(delta)
    }

    fun startWrite(): Long {
        diskStoreWriteInProgressMeter.increment()
        return NOW_NANOS
    }

    fun startFlush(): Long {
        diskStoreFlushesInProgressMeter.increment()
        return NOW_NANOS
    }

    fun incWrittenBytes(bytesWritten: Long, async: Boolean) {
        if (async) {
            diskStoreFlushToDiskBytesMeter.increment(bytesWritten)
        } else {
            diskStoreWriteToDiskBytesMeter.increment(bytesWritten)
        }
    }

    fun endWrite(start: Long): Long {
        diskStoreWriteInProgressMeter.decrement()
        diskStoreWriteToDiskMeter.increment()
        val endTime = NOW_NANOS
        diskStoreWriteToDiskTimer.recordValue(endTime - start)
        return endTime
    }

    fun endFlush(start: Long) {
        diskStoreFlushesInProgressMeter.decrement()
        diskStoreFlushToDiskMeter.increment()
        val endTime = NOW_NANOS
        diskStoreFlushToDiskTimer.recordValue(endTime - start)
    }

    fun startRead(): Long = NOW_NANOS

    fun endRead(start: Long, bytesRead: Long): Long {
        diskStoreReadFromDiskMeter.increment()
        val endTime = NOW_NANOS
        diskStoreReadFromDiskBytesMeter.increment(bytesRead)
        diskStoreReadFromDiskTimer.recordValue(endTime - start)
        return endTime
    }

    fun startRecovery(): Long {
        diskStoreRecoveryInProgressMeter.increment()
        return NOW_NANOS
    }

    fun startCompaction(): Long {
        diskStoreCompactInProgressMeter.increment()
        return NOW_NANOS
    }

    fun startOplogRead(): Long = NOW_NANOS

    fun endRecovery(start: Long, bytesRead: Long) {
        diskStoreRecoveryInProgressMeter.decrement()
        diskStoreRecoveryBytesMeter.increment(bytesRead)
        diskStoreRecoveryTimer.recordValue(NOW_NANOS - start)
    }

    fun endCompaction(start: Long) {
        diskStoreCompactInProgressMeter.decrement()
        diskStoreCompactMeter.increment()
        diskStoreCompactTimer.recordValue(NOW_NANOS - start)
    }

    fun endOplogRead(start: Long, bytesRead: Long) {
        diskStoreOplogRecoveryMeter.increment()
        diskStoreOplogRecoveryTimer.recordValue(NOW_NANOS - start)
        diskStoreOplogRecoveryBytesMeter.increment(bytesRead)
    }

    fun incRecoveredEntryCreates() {
        diskStoreRecoveryEntriesCreateMeter.increment()
    }

    fun incRecoveredEntryUpdates() {
        diskStoreRecoveryEntriesUpdateMeter.increment()
    }

    fun incRecoveredEntryDestroys() {
        diskStoreRecoveryEntriesDestroyMeter.increment()
    }

    fun incRecoveryRecordsSkipped() {
        diskStoreRecoverySkipMeter.increment()
    }

    fun incRecoveredValuesSkippedDueToLRU() {
        diskStoreRecoverySkipDueToLRUMeter.increment()
    }

    fun startRemove(): Long = NOW_NANOS


    fun endRemove(start: Long): Long {
        diskStoreEntryRemovedMeter.increment()
        val endTime = NOW_NANOS
        diskStoreEntryRemovedTimer.recordValue(endTime - start)
        return endTime
    }

    fun incOplogReads() {
        diskStoreOplogReadMeter.increment()
    }

    fun incOplogSeeks() {
        diskStoreOplogSeekMeter.increment()
    }

    fun incInactiveOplogs(delta: Int) {
        diskStoreOplogInactiveMeter.increment(delta)
    }

    fun incCompactableOplogs(delta: Int) {
        diskStoreOplogCompactableMeter.increment(delta)
    }

    fun endCompactionDeletes(count: Int, delta: Long) {
        diskStoreCompactDestroyMeter.increment(count)
        diskStoreCompactDestroyTimer.recordValue(delta)
    }

    fun endCompactionInsert(start: Long) {
        diskStoreCompactInsertMeter.increment()
        diskStoreCompactInsertTimer.recordValue(NOW_NANOS - start)
    }

    fun endCompactionUpdate(start: Long) {
        diskStoreCompactUpdateMeter.increment()
        diskStoreCompactUpdateTimer.recordValue(NOW_NANOS - start)
    }

    fun getStatTime(): Long = NOW_NANOS

    fun incOpenOplogs() {
        diskStoreOplogOpenMeter.increment()
    }

    fun decOpenOplogs() {
        diskStoreOplogOpenMeter.decrement()
    }

    fun startBackup() {
        diskStoreBackupInProgressMeter.increment()
    }

    fun endBackup() {
        diskStoreBackupInProgressMeter.decrement()
        diskStoreBackupCompletedMeter.increment()
    }
}