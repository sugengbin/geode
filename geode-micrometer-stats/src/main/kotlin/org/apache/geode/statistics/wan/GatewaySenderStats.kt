/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.statistics.wan

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter
import org.apache.geode.statistics.util.NOW_NANOS

class GatewaySenderStats(private val gateReceiverName: String) : MicrometerMeterGroup("GatewayReceiverStats-$gateReceiverName") {

    override fun getCommonTags(): Array<String> = arrayOf("gatewaySenderName", gateReceiverName)

    private val gatewaySenderEventsReceivedMeter = CounterStatisticMeter("gateway.sender.event.received.count", "Number of events received by this Sender.")
    private val gatewaySenderEventsQueueMeter = CounterStatisticMeter("gateway.sender.event.queued.count", "Number of events added to the event queue.")
    private val gatewaySenderEventsQueueTimer = TimerStatisticMeter("gateway.sender.event.queue.time", "Total time spent queueing events.", unit = "nanoseconds")
    private val gatewaySenderEventQueueSizeMeter = GaugeStatisticMeter("gateway.sender.event.queue.size", "Size of the event queue.", arrayOf("queueType", "primary"))
    private val gatewaySenderSecondaryEventQueueSizeMeter = GaugeStatisticMeter("gateway.sender.event.queue.size", "Size of secondary event queue.", arrayOf("queueType", "secondary"))
    private val gatewaySenderEventsProcessedByPQRMMeter = GaugeStatisticMeter("gateway.sender.event.processed.pqrm.count", "Total number of events processed by Parallel Queue Removal Message(PQRM).")
    private val gatewaySenderTempEventQueueSizeMeter = GaugeStatisticMeter("gateway.sender.event.queue.size", "Size of the temporary events.", arrayOf("queueType", "temp"))
    private val gatewaySenderEventsNotQueuedConflatedMeter = CounterStatisticMeter("gateway.sender.event.notqueued.conflated.count", "Number of events received but not added to the event queue because the queue already contains an event with the event's key.")
    private val gatewaySenderEventsConflatedMeter = CounterStatisticMeter("gateway.sender.event.conflated.count", "Number of events conflated from batches.")
    private val gatewaySenderEventsDistributionMeter = CounterStatisticMeter("gateway.sender.event.distribution.count", "Number of events removed from the event queue and sent.")
    private val gatewaySenderEventExceededAlertThresholdMeter = CounterStatisticMeter("gateway.sender.event.alertthreshold.count", "Number of events exceeding the alert threshold.")
    private val gatewaySenderEventsDistributionTimer = TimerStatisticMeter("gateway.sender.event.distribution.time", "Total time spent distributing batches of events to other gateway receivers.", unit = "nanoseconds")
    private val gatewaySenderEventsBatchDistributedMeter = CounterStatisticMeter("gateway.sender.batches.distributed.count", "Number of batches of events removed from the event queue and sent.")
    private val gatewaySenderEventsBatchRedistributedMeter = CounterStatisticMeter("gateway.sender.batches.redistributed.count", "Number of batches of events removed from the event queue and resent.")
    private val gatewaySenderEventsBatchResizedMeter = CounterStatisticMeter("gateway.sender.batches.resized.count", "Number of batches that were resized because they were too large")
    private val gatewaySenderUnprocessedTokenAddedByPrimaryMeter = CounterStatisticMeter("gateway.sender.unprocessed.token.secondary.added.count", "Number of tokens added to the secondary's unprocessed token map by the primary (though a listener).", arrayOf("actorType", "primary"))
    private val gatewaySenderUnprocessedEventAddedBySecondaryMeter = CounterStatisticMeter("gateway.sender.unprocessed.event.secondary.added.count", "Number of events added to the secondary's unprocessed event map by the secondary.", arrayOf("actorType", "secondary"))
    private val gatewaySenderUnprocessedEventRemovedByPrimaryMeter = CounterStatisticMeter("gateway.sender.unprocessed.event.secondary.removed.count", "Number of events removed from the secondary's unprocessed event map by the primary (though a listener).", arrayOf("actorType", "primary"))
    private val gatewaySenderUnprocessedTokenRemovedBySecondaryMeter = CounterStatisticMeter("gateway.sender.unprocessed.token.secondary.removed.count", "Number of tokens removed from the secondary's unprocessed token map by the secondary.", arrayOf("actorType", "secondary"))
    private val gatewaySenderUnprocessedEventRemovedByTimeoutMeter = CounterStatisticMeter("gateway.sender.unprocessed.event.secondary.removed.count", "Number of events removed from the secondary's unprocessed event map by a timeout.", arrayOf("actorType", "timeout"))
    private val gatewaySenderUnprocessedTokenRemovedByTimeoutMeter = CounterStatisticMeter("gateway.sender.unprocessed.token.secondary.removed.count", "Number of tokens removed from the secondary's unprocessed token map by a timeout.", arrayOf("actorType", "timeout"))
    private val gatewaySenderUnprocessedEventSizeMeter = GaugeStatisticMeter("gateway.sender.unprocessed.event.secondary.size", "Current number of entries in the secondary's unprocessed event map.")
    private val gatewaySenderUnprocessedTokenSizeMeter = GaugeStatisticMeter("gateway.sender.unprocessed.token.secondary.size", "Current number of entries in the secondary's unprocessed token map.")
    private val gatewaySenderConflationIndexSizeMeter = GaugeStatisticMeter("gateway.sender.conflation.index.size", "Current number of entries in the conflation indexes map.")
    private val gatewaySenderEventsNotQueuedMeter = CounterStatisticMeter("gateway.sender.notQueued.count", "Number of events not added to queue.")
    private val gatewaySenderEventsDroppedSenderNotRunningMeter = CounterStatisticMeter("gateway.sender.events.dropped.count", "Number of events dropped because the primary gateway sender is not running.", arrayOf("reason", "primarySenderNotRunning"))
    private val gatewaySenderEventsDroppedFilteredMeter = CounterStatisticMeter("gateway.sender.events.filtered.count", "Number of events filtered through GatewayEventFilter.")
    private val gatewaySenderLoadBalancesCompletedMeter = CounterStatisticMeter("gateway.sender.loadBalances.completed.count", "Number of load balances completed")
    private val gatewaySenderLoadBalancesInProgressMeter = GaugeStatisticMeter("gateway.sender.loadBalances.inprogress.count", "Number of load balances in progress")
    private val gatewaySenderLoadBalancesTimer = TimerStatisticMeter("gateway.sender.loadBalances.time", "Total time spent load balancing this sender", unit = "nanoseconds")
    private val gatewaySenderSynchronizationEventsQueuedMeter = CounterStatisticMeter("gateway.sender.synchronization.events.queued.count", "Number of synchronization events added to the event queue.")
    private val gatewaySenderSynchronizationEventsSentMeter = CounterStatisticMeter("gateway.sender.synchronization.events.sent.count", "Number of synchronization events provided to other members.")

    override fun initializeStaticMeters() {
        registerMeter(gatewaySenderEventsReceivedMeter)
        registerMeter(gatewaySenderEventsQueueMeter)
        registerMeter(gatewaySenderEventsQueueTimer)
        registerMeter(gatewaySenderEventQueueSizeMeter)
        registerMeter(gatewaySenderSecondaryEventQueueSizeMeter)
        registerMeter(gatewaySenderEventsProcessedByPQRMMeter)
        registerMeter(gatewaySenderTempEventQueueSizeMeter)
        registerMeter(gatewaySenderEventsNotQueuedConflatedMeter)
        registerMeter(gatewaySenderEventsConflatedMeter)
        registerMeter(gatewaySenderEventsDistributionMeter)
        registerMeter(gatewaySenderEventExceededAlertThresholdMeter)
        registerMeter(gatewaySenderEventsDistributionTimer)
        registerMeter(gatewaySenderEventsBatchDistributedMeter)
        registerMeter(gatewaySenderEventsBatchRedistributedMeter)
        registerMeter(gatewaySenderEventsBatchResizedMeter)
        registerMeter(gatewaySenderUnprocessedTokenAddedByPrimaryMeter)
        registerMeter(gatewaySenderUnprocessedEventAddedBySecondaryMeter)
        registerMeter(gatewaySenderUnprocessedEventRemovedByPrimaryMeter)
        registerMeter(gatewaySenderUnprocessedTokenRemovedBySecondaryMeter)
        registerMeter(gatewaySenderUnprocessedEventRemovedByTimeoutMeter)
        registerMeter(gatewaySenderUnprocessedTokenRemovedByTimeoutMeter)
        registerMeter(gatewaySenderUnprocessedEventSizeMeter)
        registerMeter(gatewaySenderUnprocessedTokenSizeMeter)
        registerMeter(gatewaySenderConflationIndexSizeMeter)
        registerMeter(gatewaySenderEventsNotQueuedMeter)
        registerMeter(gatewaySenderEventsDroppedSenderNotRunningMeter)
        registerMeter(gatewaySenderEventsDroppedFilteredMeter)
        registerMeter(gatewaySenderLoadBalancesCompletedMeter)
        registerMeter(gatewaySenderLoadBalancesInProgressMeter)
        registerMeter(gatewaySenderLoadBalancesTimer)
        registerMeter(gatewaySenderSynchronizationEventsQueuedMeter)
        registerMeter(gatewaySenderSynchronizationEventsSentMeter)
    }

    val eventsReceived: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsReceivedMeter.getValue()

    val eventsQueued: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsQueueMeter.getValue()

    val eventsNotQueuedConflated: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsNotQueuedConflatedMeter.getValue()

    val eventsConflatedFromBatches: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsConflatedMeter.getValue()

    val eventQueueSize: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventQueueSizeMeter.getValue()

    val secondaryEventQueueSize: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderSecondaryEventQueueSizeMeter.getValue()

    var eventsProcessedByPQRM: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsProcessedByPQRMMeter.getValue()
        set(size) {
            gatewaySenderEventsProcessedByPQRMMeter.setValue(size)
        }

    val tempEventQueueSize: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderTempEventQueueSizeMeter.getValue()


    val eventsDistributed: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsDistributionMeter.getValue()


    val eventsExceedingAlertThreshold: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventExceededAlertThresholdMeter.getValue()

    val batchesDistributed: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsBatchDistributedMeter.getValue()

    val batchesRedistributed: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsBatchRedistributedMeter.getValue()

    val batchesResized: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsBatchResizedMeter.getValue()

    val unprocessedTokensAddedByPrimary: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderUnprocessedTokenAddedByPrimaryMeter.getValue()

    val unprocessedEventsAddedBySecondary: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderUnprocessedEventAddedBySecondaryMeter.getValue()

    val unprocessedEventsRemovedByPrimary: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderUnprocessedEventRemovedByPrimaryMeter.getValue()

    val unprocessedTokensRemovedBySecondary: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderUnprocessedTokenRemovedBySecondaryMeter.getValue()

    val unprocessedEventMapSize: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderUnprocessedEventSizeMeter.getValue()

    val unprocessedTokenMapSize: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderUnprocessedTokenSizeMeter.getValue()

    val eventsNotQueued: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsNotQueuedMeter.getValue()

    val eventsDroppedDueToPrimarySenderNotRunning: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsDroppedSenderNotRunningMeter.getValue()

    val eventsFiltered: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderEventsDroppedFilteredMeter.getValue()

    val conflationIndexesMapSize: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewaySenderConflationIndexSizeMeter.getValue()

    fun incEventsReceived() {
        gatewaySenderEventsReceivedMeter.increment()
    }

    fun incEventsExceedingAlertThreshold() {
        gatewaySenderEventExceededAlertThresholdMeter.increment()
    }

    fun incBatchesRedistributed() {
        gatewaySenderEventsBatchDistributedMeter.increment()
    }

    fun incBatchesResized() {
        gatewaySenderEventsBatchResizedMeter.increment()
    }

    fun setQueueSize(size: Long) {
        gatewaySenderEventQueueSizeMeter.setValue(size)
    }

    fun setSecondaryQueueSize(size: Long) {
        gatewaySenderSecondaryEventQueueSizeMeter.setValue(size)
    }

    fun setTempQueueSize(size: Long) {
        gatewaySenderTempEventQueueSizeMeter.setValue(size)
    }

    @JvmOverloads
    fun incQueueSize(delta: Long = 1) {
        gatewaySenderEventQueueSizeMeter.increment(delta)
    }

    @JvmOverloads
    fun incSecondaryQueueSize(delta: Long = 1) {
        gatewaySenderSecondaryEventQueueSizeMeter.increment(delta)
    }

    @JvmOverloads
    fun incTempQueueSize(delta: Long = 1) {
        gatewaySenderTempEventQueueSizeMeter.increment(delta)
    }

    fun incEventsProcessedByPQRM(delta: Long) {
        gatewaySenderEventsProcessedByPQRMMeter.increment(delta)
    }


    @JvmOverloads
    fun decQueueSize(delta: Long = 1) {
        gatewaySenderEventQueueSizeMeter.increment(delta)
    }

    @JvmOverloads
    fun decSecondaryQueueSize(delta: Long = 1) {
        gatewaySenderSecondaryEventQueueSizeMeter.increment(delta)
    }

    @JvmOverloads
    fun decTempQueueSize(delta: Long = 1) {
        gatewaySenderTempEventQueueSizeMeter.increment(delta)
    }

    fun incEventsNotQueuedConflated() {
        gatewaySenderEventsNotQueuedConflatedMeter.increment()
    }

    fun incEventsConflatedFromBatches(numEvents: Long) {
        gatewaySenderEventsConflatedMeter.increment(numEvents)
    }

    fun incEventsNotQueued() {
        gatewaySenderEventsNotQueuedMeter.increment()
    }

    fun incEventsDroppedDueToPrimarySenderNotRunning() {
        gatewaySenderEventsDroppedSenderNotRunningMeter.increment()
    }

    fun incEventsFiltered() {
        gatewaySenderEventsDroppedFilteredMeter.increment()
    }

    fun incUnprocessedTokensAddedByPrimary() {
        gatewaySenderUnprocessedTokenAddedByPrimaryMeter.increment()
        incUnprocessedTokenMapSize()
    }

    fun incUnprocessedEventsAddedBySecondary() {
        gatewaySenderUnprocessedEventAddedBySecondaryMeter.increment()
        incUnprocessedEventMapSize()
    }

    fun incUnprocessedEventsRemovedByPrimary() {
        gatewaySenderUnprocessedEventRemovedByPrimaryMeter.increment()
        decUnprocessedEventMapSize()
    }

    fun incUnprocessedTokensRemovedBySecondary() {
        gatewaySenderUnprocessedTokenRemovedBySecondaryMeter.increment()
        decUnprocessedTokenMapSize()
    }

    fun incUnprocessedEventsRemovedByTimeout(count: Long) {
        gatewaySenderUnprocessedEventRemovedByTimeoutMeter.increment(count)
        decUnprocessedEventMapSize(count)
    }

    fun incUnprocessedTokensRemovedByTimeout(count: Long) {
        gatewaySenderUnprocessedTokenRemovedByTimeoutMeter.increment(count)
        decUnprocessedTokenMapSize(count)
    }

    fun clearUnprocessedMaps() {
        gatewaySenderUnprocessedEventSizeMeter.setValue(0)
        gatewaySenderUnprocessedTokenSizeMeter.setValue(0)
    }

    private fun incUnprocessedEventMapSize() {
        gatewaySenderUnprocessedEventSizeMeter.increment()
    }

    @JvmOverloads
    private fun decUnprocessedEventMapSize(count: Long = 1) {
        gatewaySenderUnprocessedEventSizeMeter.decrement(count)
    }


    private fun incUnprocessedTokenMapSize() {
        gatewaySenderUnprocessedTokenSizeMeter
    }

    @JvmOverloads
    private fun decUnprocessedTokenMapSize(decCount: Long = 1) {
        gatewaySenderUnprocessedTokenSizeMeter.decrement(decCount)
    }

    fun incConflationIndexesMapSize() {
        gatewaySenderConflationIndexSizeMeter.increment()
    }

    fun decConflationIndexesMapSize() {
        gatewaySenderConflationIndexSizeMeter.decrement()
    }

    fun startTime(): Long = NOW_NANOS

    fun endBatch(start: Long, numberOfEvents: Long) {
        // Increment event distribution time
        gatewaySenderEventsDistributionTimer.recordValue(NOW_NANOS - start)

        // Increment number of batches distributed
        gatewaySenderEventsBatchDistributedMeter.increment()

        // Increment number of events distributed
        gatewaySenderEventsDistributionMeter.increment()
    }

    fun endPut(start: Long) {
        gatewaySenderEventsQueueTimer.recordValue(NOW_NANOS - start)
        // Increment number of event queued
        gatewaySenderEventsQueueMeter.increment()
    }

    fun startLoadBalance(): Long {
        gatewaySenderLoadBalancesInProgressMeter.increment()
        return NOW_NANOS
    }

    fun endLoadBalance(start: Long) {
        gatewaySenderLoadBalancesTimer.recordValue(NOW_NANOS - start)
        gatewaySenderLoadBalancesInProgressMeter.decrement()
        gatewaySenderLoadBalancesCompletedMeter.increment()
    }

    fun incSynchronizationEventsEnqueued() {
        gatewaySenderSynchronizationEventsQueuedMeter.increment()
    }

    fun incSynchronizationEventsProvided() {
        gatewaySenderSynchronizationEventsSentMeter.increment()
    }
}
