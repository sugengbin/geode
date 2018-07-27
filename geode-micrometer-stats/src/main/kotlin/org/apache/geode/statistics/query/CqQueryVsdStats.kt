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
package org.apache.geode.statistics.query

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup

class CqQueryVsdStats(cqName: String) : MicrometerMeterGroup("CqQueryStats-$cqName") {

    private val cqInitialResultsTimeMeter = CounterStatisticMeter("cq.initialResult.time", "The total amount of time, in nanoseconds, it took to do this initial query and send the results to the client.", unit = "nanoseconds")
    private val cqInsertCountMeter = CounterStatisticMeter("cq.operation.count", "Total number of inserts done on this cq.", arrayOf("operation", "insert"))
    private val cqUpdateCountMeter = CounterStatisticMeter("cq.operation.count", "Total number of updates done on this cq.", arrayOf("operation", "update"))
    private val cqDeleteCountMeter = CounterStatisticMeter("cq.operation.count", "Total number of deletes done on this cq.", arrayOf("operation", "delete"))
    private val cqQueuedEventsMeter = GaugeStatisticMeter("cq.events.queued", "Number of events in this cq.")
    private val cqListenerInvocationsMeter = CounterStatisticMeter("cq.listener.invocations", "Total number of CqListener invocations.")
    private val cqEventsQueuedWhilstRegistrationMeter = GaugeStatisticMeter("cq.events.queued.registration", "Number of events queued while CQ registration is in progress. This is not the main cq queue but a temporary internal one used while the cq is starting up.")

    override fun initializeStaticMeters() {
        registerMeter(cqInitialResultsTimeMeter)
        registerMeter(cqInsertCountMeter)
        registerMeter(cqUpdateCountMeter)
        registerMeter(cqDeleteCountMeter)
        registerMeter(cqQueuedEventsMeter)
        registerMeter(cqListenerInvocationsMeter)
        registerMeter(cqEventsQueuedWhilstRegistrationMeter)
    }

    fun setCQInitialResultsTime(time: Long) {
        cqInitialResultsTimeMeter.increment(time)
    }

    fun incNumInserts() {
        cqInsertCountMeter.increment()
    }

    fun incNumUpdates() {
        cqUpdateCountMeter.increment()
    }

    fun incNumDeletes() {
        cqDeleteCountMeter.increment()
    }

    fun incNumHAQueuedEvents(incAmount: Long) {
        cqQueuedEventsMeter.increment(incAmount)
    }

    fun incNumCqListenerInvocations() {
        cqListenerInvocationsMeter.increment()
    }

    fun incQueuedCqListenerEvents() {
        cqEventsQueuedWhilstRegistrationMeter.increment()
    }

    fun decQueuedCqListenerEvents() {
        cqEventsQueuedWhilstRegistrationMeter.decrement()
    }
}
