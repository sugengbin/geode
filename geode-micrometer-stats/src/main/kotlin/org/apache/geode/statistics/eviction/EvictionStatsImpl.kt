package org.apache.geode.statistics.eviction

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup

abstract class EvictionStatsImpl(private val regionName: String, private val groupName: String = "EvictionStats-$regionName") : MicrometerMeterGroup(groupName), EvictionStats {
    override fun getCommonTags(): Array<String> = arrayOf("regionName", regionName)

    override fun initializeStaticMeters() {
        registerMeter(evictionLRUEvictionCountMeter)
        registerMeter(evictionLRUDestroyCountMeter)
        registerMeter(evictionLRUEvaluationsCountMeter)
        registerMeter(evictionLRUGreedyReturnsCountMeter)
    }

    private val evictionLRUEvictionCountMeter = CounterStatisticMeter("eviction.lru.evictions.count", "Number of total entry evictions triggered by LRU.")
    private val evictionLRUDestroyCountMeter = CounterStatisticMeter("eviction.lru.destroy.count", "Number of entries destroyed in the region through both destroy cache operations and eviction.")
    private val evictionLRUEvaluationsCountMeter = CounterStatisticMeter("eviction.lru.evaluation.count", "Number of entries evaluated during LRU operations.")
    private val evictionLRUGreedyReturnsCountMeter = CounterStatisticMeter("eviction.lru.greedyreturns.count", "Number of non-LRU entries evicted during LRU operations")

    override fun incEvictions() {
        evictionLRUEvictionCountMeter.increment()
    }

    override fun incEvaluations(delta: Long) {
        evictionLRUEvaluationsCountMeter.increment(delta)
    }

    override fun incDestroys() {
        evictionLRUDestroyCountMeter.increment()
    }

    override fun incGreedyReturns(delta: Long) {
        evictionLRUGreedyReturnsCountMeter.increment(delta)
    }
}