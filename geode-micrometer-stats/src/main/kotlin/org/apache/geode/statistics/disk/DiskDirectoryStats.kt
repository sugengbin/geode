package org.apache.geode.statistics.disk

import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter

class DiskDirectoryStats(owner: String) : MicrometerMeterGroup("DiskDirectoryStats-$owner") {
    override fun initializeStaticMeters() {
        registerMeter(diskDirectoryDiskSpaceCountMeter)
        registerMeter(diskDirectoryDiskMaxSpaceMeter)
        registerMeter(diskDirectoryVolumeSizeMeter)
        registerMeter(diskDirectoryVolumeFreeMeter)
        registerMeter(diskDirectoryVolumeFreeCheckMeter)
        registerMeter(diskDirectoryVolumeFreeCheckTimer)
    }

    val diskDirectoryDiskSpaceCountMeter = GaugeStatisticMeter("directory.disk.space.count", "The total number of bytes currently being used on disk in this directory for oplog files.", arrayOf("directoryOwner", owner))
    val diskDirectoryDiskMaxSpaceMeter = GaugeStatisticMeter("directory.disk.space.max", "The configured maximum number of bytes allowed in this directory for oplog files. Note that some product configurations allow this maximum to be exceeded.", arrayOf("directoryOwner", owner))
    val diskDirectoryVolumeSizeMeter = GaugeStatisticMeter("directory.volume.size", "The total size in bytes of the disk volume", arrayOf("directoryOwner", owner))
    val diskDirectoryVolumeFreeMeter = GaugeStatisticMeter("directory.volume.free.size", "The total free space in bytes on the disk volume", arrayOf("directoryOwner", owner))
    val diskDirectoryVolumeFreeCheckMeter = GaugeStatisticMeter("directory.volume.free.check.count", "The total number of disk space checks", arrayOf("directoryOwner", owner))
    val diskDirectoryVolumeFreeCheckTimer = TimerStatisticMeter("directory.volume.free.check.time", "The total time spent checking disk usage", arrayOf("directoryOwner", owner), unit = "nanoseconds")


    fun incDiskSpace(delta: Long) {
        diskDirectoryDiskSpaceCountMeter.increment(delta)
    }

    fun setMaxSpace(v: Long) {
        diskDirectoryDiskMaxSpaceMeter.setValue(v)
    }

    fun addVolumeCheck(total: Long, free: Long, time: Long) {
        diskDirectoryVolumeFreeCheckMeter.increment()
        diskDirectoryVolumeFreeCheckTimer.recordValue(time)
        diskDirectoryVolumeSizeMeter.increment(total)
        diskDirectoryVolumeFreeMeter.increment(free)
    }
}