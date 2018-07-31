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

import org.apache.geode.statistics.cache.CacheServerStats
import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.util.NOW_NANOS

//By inheritence this is a MicrometerMeterGroup
class GatewayReceiverStats(private val owner: String) : CacheServerStats(owner,"GatewayReceiverStats-$owner") {

    override fun getCommonTags(): Array<String> = arrayOf("owner", owner)

    override fun initializeStaticMeters() {
        registerMeter(gatewayReceiverBatchesDuplicateMeter)
        registerMeter(gatewayReceiverBatchesOutOfOrderMeter)
        registerMeter(gatewayReceiverEarlyAcksMeter)
        registerMeter(gatewayReceiverTotalEventsReceivedMeter)
        registerMeter(gatewayReceiverCreateEventsReceivedMeter)
        registerMeter(gatewayReceiverUpdateEventsReceivedMeter)
        registerMeter(gatewayReceiverDestroyEventsReceivedMeter)
        registerMeter(gatewayReceiverUnknownEventsReceivedMeter)
        registerMeter(gatewayReceiverExceptionsMeter)
        registerMeter(gatewayReceiverEventsRetriedMeter)
    }

    private val gatewayReceiverBatchesDuplicateMeter = CounterStatisticMeter("gateway.receiver.batches.duplicate.count", "number of batches which have already been seen by this GatewayReceiver")
    private val gatewayReceiverBatchesOutOfOrderMeter = CounterStatisticMeter("gateway.receiver.batches.outoforder.count", "number of batches which are out of order on this GatewayReceiver")
    private val gatewayReceiverEarlyAcksMeter = CounterStatisticMeter("gateway.receiver.earlyAcks.count", "number of early acknowledgements sent to gatewaySenders")
    private val gatewayReceiverTotalEventsReceivedMeter = CounterStatisticMeter("gateway.receiver.events.received.count", "total number events across the batched received by this GatewayReceiver")
    private val gatewayReceiverCreateEventsReceivedMeter = CounterStatisticMeter("gateway.receiver.events.received.count", "total number of create operations received by this GatewayReceiver", arrayOf("operationType", "create"))
    private val gatewayReceiverUpdateEventsReceivedMeter = CounterStatisticMeter("gateway.receiver.events.received.count", "total number of update operations received by this GatewayReceiver", arrayOf("operationType", "update"))
    private val gatewayReceiverDestroyEventsReceivedMeter = CounterStatisticMeter("gateway.receiver.events.received.count", "total number of destroy operations received by this GatewayReceiver", arrayOf("operationType", "destroy"))
    private val gatewayReceiverUnknownEventsReceivedMeter = CounterStatisticMeter("gateway.receiver.events.received.count", "total number of unknown operations received by this GatewayReceiver", arrayOf("operationType", "unknown"))
    private val gatewayReceiverExceptionsMeter = CounterStatisticMeter("gateway.receiver.exceptions.count", "number of exceptions occurred while porcessing the batches")
    private val gatewayReceiverEventsRetriedMeter = CounterStatisticMeter("gateway.receiver.events.retried", "total number events retried by this GatewayReceiver due to exceptions")

    val duplicateBatchesReceived: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewayReceiverBatchesDuplicateMeter.getValue()

    val outoforderBatchesReceived: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewayReceiverBatchesOutOfOrderMeter.getValue()

    val earlyAcks: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewayReceiverEarlyAcksMeter.getValue()

    val eventsReceived: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewayReceiverTotalEventsReceivedMeter.getValue()

    val createRequest: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewayReceiverCreateEventsReceivedMeter.getValue()

    val updateRequest: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewayReceiverUpdateEventsReceivedMeter.getValue()

    val destroyRequest: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewayReceiverDestroyEventsReceivedMeter.getValue()

    val unknowsOperationsReceived: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewayReceiverUnknownEventsReceivedMeter.getValue()

    val exceptionsOccurred: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewayReceiverExceptionsMeter.getValue()

    val eventsRetried: Long
        @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
        get() = gatewayReceiverEventsRetriedMeter.getValue()

    fun incDuplicateBatchesReceived() {
        gatewayReceiverBatchesDuplicateMeter.increment()
    }

    fun incOutoforderBatchesReceived() {
        gatewayReceiverBatchesOutOfOrderMeter.increment()
    }

    fun incEarlyAcks() {
        gatewayReceiverEarlyAcksMeter.increment()
    }

    fun incEventsReceived(delta: Long) {
        gatewayReceiverTotalEventsReceivedMeter.increment()
    }

    fun incCreateRequest() {
        gatewayReceiverCreateEventsReceivedMeter.increment()
    }

    fun incUpdateRequest() {
        gatewayReceiverUpdateEventsReceivedMeter.increment()
    }

    fun incDestroyRequest() {
        gatewayReceiverDestroyEventsReceivedMeter.increment()
    }

    fun incUnknowsOperationsReceived() {
        gatewayReceiverUnknownEventsReceivedMeter.increment()
    }

    fun incExceptionsOccurred() {
        gatewayReceiverExceptionsMeter.increment()
    }

    fun incEventsRetried() {
        gatewayReceiverEventsRetriedMeter.increment()
    }

    fun startTime(): Long = NOW_NANOS
}
