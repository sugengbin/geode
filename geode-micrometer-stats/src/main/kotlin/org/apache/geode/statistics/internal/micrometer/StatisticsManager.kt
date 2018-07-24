package org.apache.geode.statistics.internal.micrometer

interface StatisticsManager {
    fun registerMeterGroup(groupName:String, meterGroup: StatisticsMeterGroup)
}