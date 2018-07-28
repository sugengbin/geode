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
package org.apache.geode.statistics.cache

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter
import org.apache.geode.statistics.util.NOW_NANOS

class CacheClientNotifierStats(clientName: String) : MicrometerMeterGroup("CacheClientNotifierStats-$clientName") {

    private val clientNotifierEventsMeter = CounterStatisticMeter("client.notifier.events.count", "Number of events processed by the cache client notifier.", arrayOf("clientName", clientName))
    private val clientNotifierEventProcessedTimer = TimerStatisticMeter("client.notifier.events.processed.time", "Total time spent by the cache client notifier processing events.", arrayOf("clientName", clientName), unit = "nanoseconds")
    private val clientNotifierClientRegistrationMeter = GaugeStatisticMeter("client.notifier.client.registration.count", "Number of clients that have registered for updates.", arrayOf("clientName", clientName))
    private val clientNotifierClientRegistrationTimer = TimerStatisticMeter("client.notifier.client.registration.time", "Total time spent doing client registrations.", arrayOf("clientName", clientName), unit = "nanoseconds")
    private val clientNotifierHealthMonitorRegistrationMeter = GaugeStatisticMeter("client.notifier.healthmonitor.registration.count", "Number of client Register.", arrayOf("clientName", clientName))
    private val clientNotifierHealthMonitorUnregistrationMeter = GaugeStatisticMeter("client.notifier.healthmonitor.unregistration.count", "Number of client UnRegister.", arrayOf("clientName", clientName))
    private val clientNotifierDurableReconnectionMeter = CounterStatisticMeter("client.notifier.durable.reconnection.count", "Number of times the same durable client connects to the server", arrayOf("clientName", clientName))
    private val clientNotifierDurableQueueDropMeter = CounterStatisticMeter("client.notifier.durable.queue.drop.count", "Number of times client queue for a particular durable client is dropped", arrayOf("clientName", clientName))
    private val clientNotifierDurableQueueSizeMeter = CounterStatisticMeter("client.notifier.durable.queue.count", "Number of events enqueued in queue for a durable client ", arrayOf("clientName", clientName))
    private val clientNotifierCQProcessingTimer = TimerStatisticMeter("client.notifier.cq.processing.time", "Total time spent by the cache client notifier processing cqs.", arrayOf("clientName", clientName), unit = "nanoseconds")
    private val clientNotifierCompiledQueryMeter = GaugeStatisticMeter("client.notifier.compiledquery.count", "Number of compiled queries maintained.", arrayOf("clientName", clientName))
    private val clientNotifierCompiledQueryUsedMeter = CounterStatisticMeter("client.notifier.compiledquery.used.count", "Number of times compiled queries are used.", arrayOf("clientName", clientName))

    override fun initializeStaticMeters() {
        registerMeter(clientNotifierEventsMeter)
        registerMeter(clientNotifierEventProcessedTimer)
        registerMeter(clientNotifierClientRegistrationMeter)
        registerMeter(clientNotifierClientRegistrationTimer)
        registerMeter(clientNotifierHealthMonitorRegistrationMeter)
        registerMeter(clientNotifierHealthMonitorUnregistrationMeter)
        registerMeter(clientNotifierDurableReconnectionMeter)
        registerMeter(clientNotifierDurableQueueDropMeter)
        registerMeter(clientNotifierDurableQueueSizeMeter)
        registerMeter(clientNotifierCQProcessingTimer)
        registerMeter(clientNotifierCompiledQueryMeter)
        registerMeter(clientNotifierCompiledQueryUsedMeter)
    }

    fun startTime(): Long = NOW_NANOS

    fun endEvent(start: Long) {
        clientNotifierEventsMeter.increment()
        clientNotifierEventProcessedTimer.recordValue(NOW_NANOS - start)
    }

    fun endClientRegistration(start: Long) {
        clientNotifierClientRegistrationMeter.increment()
        clientNotifierClientRegistrationTimer.recordValue(NOW_NANOS - start)
    }

    fun endCqProcessing(start: Long) {
        clientNotifierCQProcessingTimer.recordValue(NOW_NANOS - start)
    }

    fun incClientRegisterRequests() {
        clientNotifierHealthMonitorRegistrationMeter.increment()
    }

    fun incDurableReconnectionCount() {
        clientNotifierDurableReconnectionMeter.increment()
    }

    fun incQueueDroppedCount() {
        clientNotifierDurableQueueDropMeter.increment()
    }

    fun incEventEnqueuedWhileClientAwayCount() {
        clientNotifierDurableQueueSizeMeter.increment()
    }

    fun incClientUnRegisterRequests() {
        clientNotifierHealthMonitorUnregistrationMeter.increment()
    }

    fun incCompiledQueryCount(count: Long) {
        clientNotifierCompiledQueryMeter.increment(count)
    }

    fun incCompiledQueryUsedCount(count: Long) {
        clientNotifierCompiledQueryUsedMeter.increment(count)
    }
}
