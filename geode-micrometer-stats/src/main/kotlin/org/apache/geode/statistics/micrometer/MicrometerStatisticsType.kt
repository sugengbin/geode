package org.apache.geode.statistics.micrometer

import org.apache.geode.statistics.StatisticDescriptor
import org.apache.geode.statistics.StatisticsType
import org.apache.geode.statistics.internal.micrometer.impl.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.impl.MicrometerStatisticMeter

class MicrometerStatisticsType(private val name: String,
                               private val description: String,
                               private val statistics: Array<StatisticDescriptor>) : StatisticsType, MicrometerMeterGroup(groupName = name) {

    private val statsArray: Array<StatisticDescriptor> = statistics
//    private val statsIdToNameMap = hashMapOf<Int, String>()
    private val statsNameToIdMap = hashMapOf<String, Int>()

    init {
        statistics.forEachIndexed { index, statisticDescriptor ->
            run {
                statisticDescriptor as MicrometerStatisticMeter
                statisticDescriptor.meterId = index
//                statsIdToNameMap[index] = statisticDescriptor.name
                statsNameToIdMap[statisticDescriptor.name] = index
                registerMeter(statisticDescriptor)
            }
        }
    }

    override fun getName() = name
    override fun getDescription() = description
    override fun getStatistics() = statistics

    override fun nameToId(name: String): Int = statsNameToIdMap[name]
            ?: throw IllegalArgumentException("Stat does not exist for name: $name in group: ${this.name}")

    override fun nameToDescriptor(name: String): StatisticDescriptor = statsArray[nameToId(name)]

    fun getStatsForId(id: Int) = statsArray[id] as MicrometerStatisticMeter
}