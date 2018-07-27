package org.apache.geode.statistics

interface StatisticsManager {
    fun registerMeterGroup(groupName:String, meterGroup: StatisticsMeterGroup)
}