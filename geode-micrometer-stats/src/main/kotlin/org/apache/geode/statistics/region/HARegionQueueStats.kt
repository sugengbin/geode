package org.apache.geode.statistics.region

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup

class HARegionQueueStats(queueName: String) : MicrometerMeterGroup("ClientSubscriptionStats-$queueName") {
    override fun initializeStaticMeters() {
        registerMeter(haRegionQueueEventsQueuedMeter)
        registerMeter(haRegionQueueEventsConflatedMeter)
        registerMeter(haRegionQueueMarkerEventsConflatedMeter)
        registerMeter(haRegionQueueEventsRemovedMeter)
        registerMeter(haRegionQueueEventsTakenMeter)
        registerMeter(haRegionQueueEventsExpiredMeter)
        registerMeter(haRegionQueueEventsTakenQRMMeter)
        registerMeter(haRegionThreadIdentifiersMeter)
        registerMeter(haRegionQueueEventsDispatchedMeter)
        registerMeter(haRegionQueueEventsVoidRemovalMeter)
        registerMeter(haRegionQueueEventsViolationMeter)
    }

    private val haRegionQueueEventsQueuedMeter = CounterStatisticMeter("ha.region.queue.events.queued.count", "Number of events added to queue.", arrayOf("queueName", queueName))
    private val haRegionQueueEventsConflatedMeter = CounterStatisticMeter("ha.region.queue.events.conflated.count", "Number of events conflated for the queue.", arrayOf("queueName", queueName))
    private val haRegionQueueMarkerEventsConflatedMeter = CounterStatisticMeter("ha.region.queue.events.conflated.count", "Number of marker events conflated for the queue.", arrayOf("queueName", queueName, "eventType", "marker"))
    private val haRegionQueueEventsRemovedMeter = CounterStatisticMeter("ha.region.queue.events.removed.count", "Number of events removed from the queue.", arrayOf("queueName", queueName))
    private val haRegionQueueEventsTakenMeter = CounterStatisticMeter("ha.region.queue.events.taken.count", "Number of events taken from the queue.", arrayOf("queueName", queueName))
    private val haRegionQueueEventsExpiredMeter = CounterStatisticMeter("ha.region.queue.events.expired.count", "Number of events expired from the queue.", arrayOf("queueName", queueName))
    private val haRegionQueueEventsTakenQRMMeter = CounterStatisticMeter("ha.region.queue.events.taken.count", "Number of events removed by QRM message.", arrayOf("queueName", queueName, "subscriber", "qrm"))
    private val haRegionThreadIdentifiersMeter = CounterStatisticMeter("ha.region.thread.identifier.count", "Number of ThreadIdenfier objects for the queue.", arrayOf("queueName", queueName))
    private val haRegionQueueEventsDispatchedMeter = CounterStatisticMeter("ha.region.queue.events.dispatched.count", "Number of events that have been dispatched.", arrayOf("queueName", queueName))
    private val haRegionQueueEventsVoidRemovalMeter = CounterStatisticMeter("ha.region.queue.events.void.removal.count", "Number of void removals from the queue.", arrayOf("queueName", queueName))
    private val haRegionQueueEventsViolationMeter = CounterStatisticMeter("ha.region.queue.events.violation.count", "Number of events that has violated sequence.", arrayOf("queueName", queueName))

    fun incEventsEnqued() {
        haRegionQueueEventsQueuedMeter.increment()
    }

    fun incEventsConflated() {
        haRegionQueueEventsConflatedMeter.increment()
    }

    fun incMarkerEventsConflated() {
        haRegionQueueMarkerEventsConflatedMeter.increment()
    }

    fun incEventsRemoved() {
        haRegionQueueEventsRemovedMeter.increment()
    }

    fun incEventsTaken() {
        haRegionQueueEventsTakenMeter.increment()
    }

    fun incEventsExpired() {
        haRegionQueueEventsExpiredMeter.increment()
    }

    fun incEventsRemovedByQrm() {
        haRegionQueueEventsTakenQRMMeter.increment()
    }

    fun incThreadIdentifiers() {
        haRegionThreadIdentifiersMeter.increment()
    }

    fun decThreadIdentifiers() {
        haRegionThreadIdentifiersMeter.decrement()
    }

    fun incEventsDispatched() {
        haRegionQueueEventsDispatchedMeter.increment()
    }

    fun incNumVoidRemovals() {
        haRegionQueueEventsVoidRemovalMeter.increment()
    }

    fun incNumSequenceViolated() {
        haRegionQueueEventsViolationMeter.increment()
    }
}
