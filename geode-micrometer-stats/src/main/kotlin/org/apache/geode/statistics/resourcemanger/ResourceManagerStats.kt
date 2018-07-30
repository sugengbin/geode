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
package org.apache.geode.statistics.resourcemanger

import org.apache.geode.distributed.internal.PoolStatHelper
import org.apache.geode.distributed.internal.QueueStatHelper
import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter
import org.apache.geode.statistics.util.NOW_NANOS

class ResourceManagerStats : MicrometerMeterGroup("ResourceManagerStats") {

    private val resourceRebalanceInProgressMeter = GaugeStatisticMeter("manager.resources.rebalance.inprogress.count", "Current number of cache rebalance operations being directed by this process.")
    private val resourceRebalanceCompletedMeter = CounterStatisticMeter("manager.resources.rebalance.completed.count", "Total number of cache rebalance operations directed by this process.")
    private val resourceAutoRebalanceAttemptsMeter = CounterStatisticMeter("manager.resources.rebalance.auto.attempts.count", "Total number of cache auto-rebalance attempts.")
    private val resourceRebalanceTimer = TimerStatisticMeter("manager.resources.rebalance.time", "Total time spent directing cache rebalance operations.", unit = "nanoseconds")

    private val resourceRebalanceBucketCreateInProgressMeter = GaugeStatisticMeter("manager.resources.rebalance.bucket.create.inprogress.count", "Current number of bucket create operations being directed for rebalancing.")
    private val resourceRebalanceBucketCreateCompletedMeter = CounterStatisticMeter("manager.resources.rebalance.bucket.create.complete.count", "Total number of bucket create operations directed for rebalancing.")
    private val resourceRebalanceBucketCreateFailedMeter = CounterStatisticMeter("manager.resources.rebalance.bucket.create.failed.count", "Total number of bucket create operations directed for rebalancing that failed.")
    private val resourceRebalanceBucketCreateTimer = TimerStatisticMeter("manager.resources.rebalance.bucket.create.time", "Total time spent directing bucket create operations for rebalancing.", unit = "nanoseconds")
    private val resourceRebalanceBucketCreateBytesMeter = CounterStatisticMeter("manager.resources.rebalance.bucket.create.bytes", "Total bytes created while directing bucket create operations for rebalancing.", unit = "bytes")

    private val resourceRebalanceBucketRemoveInProgressMeter = GaugeStatisticMeter("manager.resources.rebalance.bucket.remove.inprogress.count", "Current number of bucket remove operations being directed for rebalancing.")
    private val resourceRebalanceBucketRemoveCompletedMeter = CounterStatisticMeter("manager.resources.rebalance.bucket.remove.complete.count", "Total number of bucket remove operations directed for rebalancing.")
    private val resourceRebalanceBucketRemoveFailedMeter = CounterStatisticMeter("manager.resources.rebalance.bucket.remove.failed.count", "Total number of bucket remove operations directed for rebalancing that failed.")
    private val resourceRebalanceBucketRemoveTimer = TimerStatisticMeter("manager.resources.rebalance.bucket.remove.time", "Total time spent directing bucket remove operations for rebalancing.", unit = "nanoseconds")
    private val resourceRebalanceBucketRemoveBytesMeter = CounterStatisticMeter("manager.resources.rebalance.bucket.remove.bytes", "Total bytes removed while directing bucket remove operations for rebalancing.", unit = "bytes")

    private val resourceRebalanceBucketTransferInProgressMeter = GaugeStatisticMeter("manager.resources.rebalance.bucket.transfer.inprogress.count", "Current number of bucket transfer operations being directed for rebalancing.")
    private val resourceRebalanceBucketTransferCompletedMeter = CounterStatisticMeter("manager.resources.rebalance.bucket.transfer.complete.count", "Total number of bucket transfer operations directed for rebalancing.")
    private val resourceRebalanceBucketTransferFailedMeter = CounterStatisticMeter("manager.resources.rebalance.bucket.transfer.failed.count", "Total number of bucket transfer operations directed for rebalancing that failed.")
    private val resourceRebalanceBucketTransferTimer = TimerStatisticMeter("manager.resources.rebalance.bucket.transfer.time", "Total time spent directing bucket transfer operations for rebalancing.", unit = "nanoseconds")
    private val resourceRebalanceBucketTransferBytesMeter = CounterStatisticMeter("manager.resources.rebalance.bucket.transfer.bytes", "Total bytes transfered while directing bucket transfer operations for rebalancing.", unit = "bytes")

    private val resourceRebalancePrimaryTransferInProgressMeter = GaugeStatisticMeter("manager.resources.rebalance.primary.transfer.inprogress.count", "Current number of primary transfer operations being directed for rebalancing.")
    private val resourceRebalancePrimaryTransferCompletedMeter = CounterStatisticMeter("manager.resources.rebalance.primary.transfer.complete.count", "Total number of primary transfer operations directed for rebalancing.")
    private val resourceRebalancePrimaryTransferFailedMeter = CounterStatisticMeter("manager.resources.rebalance.primary.transfer.failed.count", "Total number of primary transfer operations directed for rebalancing that failed.")
    private val resourceRebalancePrimaryTransferTimer = TimerStatisticMeter("manager.resources.rebalance.primary.transfer.time", "Total time spent directing primary transfer operations for rebalancing.", unit = "nanoseconds")
    private val resourceRebalancePrimaryTransferChangeMeter = CounterStatisticMeter("manager.resources.rebalance.primary.transfer.change.count", "The number of times that membership has changed during a rebalance")

    private val resourceHeapAboveCriticalThresholdMeter = GaugeStatisticMeter("manager.resource.heap.event.count", "Total number of times the heap usage went over critical threshold.", arrayOf("eventType", "critical", "thresholdTrigger", "above"))
    private val resourceOffheapAboveCriticalThresholdMeter = GaugeStatisticMeter("manager.resource.offheap.event.count", "Total number of times off-heap usage went over critical threshold.", arrayOf("eventType", "critical", "thresholdTrigger", "above"))
    private val resourceHeapBelowCriticalThresholdMeter = GaugeStatisticMeter("manager.resource.heap.event.count", "Total number of times the heap usage fell below critical threshold.", arrayOf("eventType", "critical", "thresholdTrigger", "below"))
    private val resourceOffheapBelowCriticalThresholdMeter = GaugeStatisticMeter("manager.resource.offheap.event.count", "Total number of times off-heap usage fell below critical threshold.", arrayOf("eventType", "critical", "thresholdTrigger", "below"))
    private val resourceHeapAboveEvictionStartMeter = GaugeStatisticMeter("manager.resource.heap.start.count", "Total number of times heap usage went over eviction threshold.", arrayOf("eventType", "eviction"))
    private val resourceOffheapAboveEvictionStartMeter = GaugeStatisticMeter("manager.resource.offheap.start.count", "Total number of times off-heap usage went over eviction threshold.", arrayOf("eventType", "eviction"))
    private val resourceHeapAboveEvictionStopMeter = GaugeStatisticMeter("manager.resource.heap.stop.count", "Total number of times heap usage fell below eviction threshold.", arrayOf("eventType", "eviction"))
    private val resourceOffheapAboveEvictionStopMeter = GaugeStatisticMeter("manager.resource.offheap.stop.count", "Total number of times off-heap usage fell below eviction threshold.", arrayOf("eventType", "eviction"))

    private val resourceHeapCriticalThresholdMaxBytesMeter = GaugeStatisticMeter("manager.resource.heap.threshold.bytes", "The currently set heap critical threshold value in bytes", arrayOf("eventType", "critical"))
    private val resourceOffheapCriticalThresholdMaxBytesMeter = GaugeStatisticMeter("manager.resource.offheap.threshold.bytes", "The currently set off-heap critical threshold value in bytes", arrayOf("eventType", "critical"))
    private val resourceHeapEvictionThresholdMaxBytesMeter = GaugeStatisticMeter("manager.resource.heap.threshold.bytes", "The currently set heap eviction threshold value in bytes", arrayOf("eventType", "eviction"))
    private val resourceOffheapEvictionThresholdMaxBytesMeter = GaugeStatisticMeter("manager.resource.offheap.threshold.bytes", "The currently set off-heap eviction threshold value in bytes", arrayOf("eventType", "eviction"))

    private val resourceHeapTenuredUsedBytesMeter = GaugeStatisticMeter("manager.resource.heap.tenured.used.bytes", "Total memory used in the tenured/old space", unit = "bytes")
    private val resourceEventsDeliveredMeter = CounterStatisticMeter("manager.resource.events.delivered.count", "Total number of resource events delivered to listeners")
    private val resourceEventQueueSizeMeter = GaugeStatisticMeter("manager.resource.events.queue.size", "Pending events for thresholdEventProcessor thread")
    private val resourceEventProcessorThreadsMeter = GaugeStatisticMeter("manager.resource.events.processor.thread.count", "Number of jobs currently being processed by the thresholdEventProcessorThread")
    private val resourceThreadsStuckMeter = GaugeStatisticMeter("manager.resource.threads.stuck.count", "Number of running threads that have not changed state within the thread-monitor-time-limit-ms interval.")

    override fun initializeStaticMeters() {
        registerMeter(resourceRebalanceInProgressMeter)
        registerMeter(resourceRebalanceCompletedMeter)
        registerMeter(resourceAutoRebalanceAttemptsMeter)
        registerMeter(resourceRebalanceTimer)

        registerMeter(resourceRebalanceBucketCreateInProgressMeter)
        registerMeter(resourceRebalanceBucketCreateCompletedMeter)
        registerMeter(resourceRebalanceBucketCreateFailedMeter)
        registerMeter(resourceRebalanceBucketCreateTimer)
        registerMeter(resourceRebalanceBucketCreateBytesMeter)

        registerMeter(resourceRebalanceBucketRemoveInProgressMeter)
        registerMeter(resourceRebalanceBucketRemoveCompletedMeter)
        registerMeter(resourceRebalanceBucketRemoveFailedMeter)
        registerMeter(resourceRebalanceBucketRemoveTimer)
        registerMeter(resourceRebalanceBucketRemoveBytesMeter)

        registerMeter(resourceRebalanceBucketTransferInProgressMeter)
        registerMeter(resourceRebalanceBucketTransferCompletedMeter)
        registerMeter(resourceRebalanceBucketTransferFailedMeter)
        registerMeter(resourceRebalanceBucketTransferTimer)
        registerMeter(resourceRebalanceBucketTransferBytesMeter)

        registerMeter(resourceRebalancePrimaryTransferInProgressMeter)
        registerMeter(resourceRebalancePrimaryTransferCompletedMeter)
        registerMeter(resourceRebalancePrimaryTransferFailedMeter)
        registerMeter(resourceRebalancePrimaryTransferTimer)
        registerMeter(resourceRebalancePrimaryTransferChangeMeter)

        registerMeter(resourceHeapAboveCriticalThresholdMeter)
        registerMeter(resourceOffheapAboveCriticalThresholdMeter)
        registerMeter(resourceHeapBelowCriticalThresholdMeter)
        registerMeter(resourceOffheapBelowCriticalThresholdMeter)
        registerMeter(resourceHeapAboveEvictionStartMeter)
        registerMeter(resourceOffheapAboveEvictionStartMeter)
        registerMeter(resourceHeapAboveEvictionStopMeter)
        registerMeter(resourceOffheapAboveEvictionStopMeter)

        registerMeter(resourceHeapCriticalThresholdMaxBytesMeter)
        registerMeter(resourceOffheapCriticalThresholdMaxBytesMeter)
        registerMeter(resourceHeapEvictionThresholdMaxBytesMeter)
        registerMeter(resourceOffheapEvictionThresholdMaxBytesMeter)

        registerMeter(resourceHeapTenuredUsedBytesMeter)
        registerMeter(resourceEventsDeliveredMeter)
        registerMeter(resourceEventQueueSizeMeter)
        registerMeter(resourceEventProcessorThreadsMeter)
        registerMeter(resourceThreadsStuckMeter)
    }

    val rebalancesInProgress: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalanceInProgressMeter.getValue()

    val rebalancesCompleted: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalanceCompletedMeter.getValue()

    val autoRebalanceAttempts: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceAutoRebalanceAttemptsMeter.getValue()

    val rebalanceBucketCreatesInProgress: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalanceBucketCreateInProgressMeter.getValue()

    val rebalanceBucketCreatesCompleted: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalanceBucketCreateCompletedMeter.getValue()

    val rebalanceBucketCreatesFailed: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalanceBucketCreateFailedMeter.getValue()

    val rebalanceBucketCreateBytes: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalanceBucketCreateBytesMeter.getValue()

    val rebalanceBucketTransfersInProgress: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalanceBucketTransferInProgressMeter.getValue()

    val rebalanceBucketTransfersCompleted: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalanceBucketTransferCompletedMeter.getValue()

    val rebalanceBucketTransfersFailed: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalanceBucketTransferFailedMeter.getValue()

    val rebalanceBucketTransfersBytes: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalanceBucketTransferBytesMeter.getValue()

    val rebalancePrimaryTransfersInProgress: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalancePrimaryTransferInProgressMeter.getValue()

    val rebalancePrimaryTransfersCompleted: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalancePrimaryTransferCompletedMeter.getValue()

    val rebalancePrimaryTransfersFailed: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceRebalancePrimaryTransferFailedMeter.getValue()

    val resourceEventsDelivered: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceEventsDeliveredMeter.getValue()

    val heapCriticalEvents: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceHeapAboveCriticalThresholdMeter.getValue()

    val offHeapCriticalEvents: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceOffheapAboveCriticalThresholdMeter.getValue()

    val heapSafeEvents: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceHeapBelowCriticalThresholdMeter.getValue()

    val offHeapSafeEvents: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceOffheapBelowCriticalThresholdMeter.getValue()

    val evictionStartEvents: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceHeapAboveEvictionStartMeter.getValue()

    val offHeapEvictionStartEvents: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceOffheapAboveEvictionStartMeter.getValue()

    val evictionStopEvents: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceHeapAboveEvictionStopMeter.getValue()

    val offHeapEvictionStopEvents: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceOffheapAboveEvictionStopMeter.getValue()

    val criticalThreshold: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceHeapCriticalThresholdMaxBytesMeter.getValue()

    val offHeapCriticalThreshold: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceOffheapCriticalThresholdMaxBytesMeter.getValue()

    val evictionThreshold: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceHeapEvictionThresholdMaxBytesMeter.getValue()

    val offHeapEvictionThreshold: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceOffheapEvictionThresholdMaxBytesMeter.getValue()

    val tenuredHeapUsed: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceHeapTenuredUsedBytesMeter.getValue()

    val resourceEventQueueSize: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceEventQueueSizeMeter.getValue()

    val thresholdEventProcessorThreadJobs: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceEventProcessorThreadsMeter.getValue()

    val resourceEventQueueStatHelper: QueueStatHelper
        get() = object : QueueStatHelper {
            override fun add() {
                incResourceEventQueueSize(1)
            }

            override fun remove() {
                incResourceEventQueueSize(-1)
            }

            override fun remove(count: Int) {
                incResourceEventQueueSize(-1 * count.toLong())
            }
        }

    val resourceEventPoolStatHelper: PoolStatHelper
        get() = object : PoolStatHelper {
            override fun endJob() {
                incThresholdEventProcessorThreadJobs(-1)
            }

            override fun startJob() {
                incThresholdEventProcessorThreadJobs(1)
            }
        }


    var numThreadStuck: Long
        @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
        get() = resourceThreadsStuckMeter.getValue()
        set(value) {
            resourceThreadsStuckMeter.setValue(value)
        }


    fun startRebalance(): Long {
        resourceRebalanceInProgressMeter.increment()
        return NOW_NANOS
    }

    fun incAutoRebalanceAttempts() {
        resourceAutoRebalanceAttemptsMeter.increment()
    }

    fun endRebalance(start: Long) {
        resourceRebalanceTimer.recordValue(NOW_NANOS - start)
        resourceRebalanceInProgressMeter.decrement()
        resourceRebalanceCompletedMeter.increment()
    }

    fun startBucketCreate(regions: Long) {
        resourceRebalanceBucketCreateInProgressMeter.increment(regions)
    }

    fun endBucketCreate(regions: Long, success: Boolean, bytes: Long, elapsed: Long) {
        resourceRebalanceBucketCreateInProgressMeter.decrement(regions)
        resourceRebalanceBucketCreateTimer.recordValue(elapsed)
        if (success) {
            resourceRebalanceBucketCreateCompletedMeter.increment(regions)
            resourceRebalanceBucketCreateBytesMeter.increment(bytes)
        } else {
            resourceRebalanceBucketCreateFailedMeter.increment(regions)
        }
    }

    fun startBucketRemove(regions: Long) {
        resourceRebalanceBucketRemoveInProgressMeter.increment(regions)
    }

    fun endBucketRemove(regions: Long, success: Boolean, bytes: Long, elapsed: Long) {
        resourceRebalanceBucketRemoveInProgressMeter.decrement(regions)
        resourceRebalanceBucketRemoveTimer.recordValue(elapsed)
        if (success) {
            resourceRebalanceBucketRemoveCompletedMeter.increment(regions)
            resourceRebalanceBucketRemoveBytesMeter.increment(bytes)
        } else {
            resourceRebalanceBucketRemoveFailedMeter.increment(regions)
        }
    }

    fun startBucketTransfer(regions: Long) {
        resourceRebalanceBucketTransferInProgressMeter.increment(regions)
    }

    fun endBucketTransfer(regions: Long, success: Boolean, bytes: Long, elapsed: Long) {
        resourceRebalanceBucketTransferInProgressMeter.decrement(regions)
        resourceRebalanceBucketTransferTimer.recordValue(elapsed)
        if (success) {
            resourceRebalanceBucketTransferCompletedMeter.increment(regions)
            resourceRebalanceBucketTransferBytesMeter.increment(bytes)
        } else {
            resourceRebalanceBucketTransferFailedMeter.increment(regions)
        }
    }

    fun startPrimaryTransfer(regions: Long) {
        resourceRebalancePrimaryTransferInProgressMeter.increment(regions)
    }

    fun endPrimaryTransfer(regions: Long, success: Boolean, elapsed: Long) {
        resourceRebalancePrimaryTransferInProgressMeter.decrement(regions)
        resourceRebalancePrimaryTransferTimer.recordValue(elapsed)
        if (success) {
            resourceRebalancePrimaryTransferCompletedMeter.increment(regions)
        } else {
            resourceRebalancePrimaryTransferFailedMeter.increment(regions)
        }
    }

    fun incRebalanceMembershipChanges(delta: Long) {
        resourceRebalancePrimaryTransferChangeMeter.increment(delta)
    }

    @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
    fun getRebalanceMembershipChanges(): Long = resourceRebalancePrimaryTransferChangeMeter.getValue()

    fun incResourceEventsDelivered() {
        resourceEventsDeliveredMeter.increment()
    }

    fun incHeapCriticalEvents() {
        resourceHeapAboveCriticalThresholdMeter.increment()
    }

    fun incOffHeapCriticalEvents() {
        resourceOffheapAboveCriticalThresholdMeter.increment()
    }

    fun incHeapSafeEvents() {
        resourceHeapBelowCriticalThresholdMeter.increment()
    }

    fun incOffHeapSafeEvents() {
        resourceOffheapBelowCriticalThresholdMeter.increment()
    }

    fun incEvictionStartEvents() {
        resourceHeapAboveEvictionStartMeter.increment()
    }

    fun incOffHeapEvictionStartEvents() {
        resourceOffheapAboveEvictionStartMeter.increment()
    }

    fun incEvictionStopEvents() {
        resourceHeapAboveEvictionStopMeter.increment()
    }

    fun incOffHeapEvictionStopEvents() {
        resourceOffheapAboveEvictionStopMeter.increment()
    }

    fun changeCriticalThreshold(newValue: Long) {
        resourceHeapCriticalThresholdMaxBytesMeter.setValue(newValue)
    }

    fun changeOffHeapCriticalThreshold(newValue: Long) {
        resourceOffheapCriticalThresholdMaxBytesMeter.setValue(newValue)
    }

    fun changeEvictionThreshold(newValue: Long) {
        resourceHeapEvictionThresholdMaxBytesMeter.setValue(newValue)
    }

    fun changeOffHeapEvictionThreshold(newValue: Long) {
        resourceOffheapEvictionThresholdMaxBytesMeter.setValue(newValue)
    }

    fun changeTenuredHeapUsed(newValue: Long) {
        resourceHeapTenuredUsedBytesMeter.setValue(newValue)
    }

    fun incResourceEventQueueSize(delta: Long) {
        resourceEventQueueSizeMeter.increment(delta)
    }

    fun incThresholdEventProcessorThreadJobs(delta: Long) {
        resourceEventProcessorThreadsMeter.increment(delta)
    }
}
