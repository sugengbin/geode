package org.apache.geode.statistics

import org.apache.geode.statistics.function.FunctionStats
import java.util.concurrent.ConcurrentHashMap

class InternalDistributedSystemStats {

    private val functionExecutionStatsMap = ConcurrentHashMap<String, FunctionStats>()

    fun getFunctionStats(textId: String): FunctionStats =
            functionExecutionStatsMap[textId] ?: run {
                val functionStats = FunctionStats(textId)
                functionExecutionStatsMap[textId] = functionStats
                functionStats
            }
}