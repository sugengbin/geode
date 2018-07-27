package org.apache.geode.statistics.client.connection

import org.apache.geode.internal.cache.tier.sockets.MessageStats
import org.apache.geode.statistics.ScalarStatisticsMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter

class ConnectionStats : MicrometerMeterGroup, MessageStats {
    override fun decMessagesBeingReceived(bytes: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val clientStats: ClientStats
    private val clientSendStats: ClientSendStats
    private val poolStats: PoolStats

    constructor(poolName: String, poolStats: PoolStats) : super("ClientConnectionStats") {
        this.clientStats = ClientStats(poolName)
        this.clientSendStats = ClientSendStats(poolName)
        this.poolStats = poolStats
    }

    override fun initializeStaticMeters() {
        registerMeterGroup(clientStats)
        registerMeterGroup(clientSendStats)
        registerMeterGroup(poolStats)
    }

    private fun startOperation(clientInProgressMeter: ScalarStatisticsMeter, clientSendInProgressMeter: ScalarStatisticsMeter): Long {
        clientInProgressMeter.increment()
        clientSendInProgressMeter.increment()
        startClientOp()
        return System.nanoTime()
    }

    private fun endOperationStats(startTime: Long, timedOut: Boolean, failed: Boolean,
                                  inProgressMeter: ScalarStatisticsMeter, failureMeter: ScalarStatisticsMeter,
                                  successMeter: ScalarStatisticsMeter, timer: TimerStatisticMeter,
                                  timeoutMeter: ScalarStatisticsMeter) {
        val timeInNanos = System.nanoTime() - startTime
        endClientOp(timeInNanos, timedOut, failed)
        inProgressMeter.decrement()
        if (failed) {
            failureMeter.increment()
        } else if (timedOut) {
            timeoutMeter.increment()
        } else {
            successMeter.increment()
        }
        timer.recordValue(timeInNanos)
    }

    private fun endOperationSendStats(startTime: Long, failed: Boolean,
                                      inProgressMeter: ScalarStatisticsMeter, failureMeter: ScalarStatisticsMeter,
                                      successMeter: ScalarStatisticsMeter, timer: TimerStatisticMeter) {
        val timeInNanos = System.nanoTime() - startTime
        endClientOpSend(timeInNanos, failed)
        inProgressMeter.decrement()
        if (failed) {
            failureMeter.increment()
        } else {
            successMeter.increment()
        }
        timer.recordValue(timeInNanos)
    }

    fun startGet(): Long = startOperation(clientStats.operationGetInProgressMeter, clientSendStats.operationGetSendInProgressMeter)

    fun endGetSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationGetSendInProgressMeter,
                clientSendStats.operationGetSendFailedMeter, clientSendStats.operationGetSendCompletedMeter,
                clientSendStats.operationGetSendTimerMeter)
    }

    fun endGet(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationGetInProgressMeter,
                clientStats.operationGetFailureCountMeter, clientStats.operationGetCountMeter,
                clientStats.operationGetTimer, clientStats.operationGetTimeoutCountMeter)
    }

    fun startPut(): Long = startOperation(clientStats.operationPutInProgressMeter, clientSendStats.operationPutSendInProgressMeter)

    fun endPutSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationPutSendInProgressMeter,
                clientSendStats.operationPutSendFailedMeter, clientSendStats.operationPutSendCompletedMeter,
                clientSendStats.operationPutSendTimerMeter)
    }

    fun endPut(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationPutInProgressMeter,
                clientStats.operationPutFailureCountMeter, clientStats.operationPutCountMeter,
                clientStats.operationPutTimer, clientStats.operationPutTimeoutCountMeter)
    }

    fun startDestroy(): Long = startOperation(clientStats.operationDestroyInProgressMeter, clientSendStats.operationDestroySendInProgressMeter)

    fun endDestroySend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationDestroySendInProgressMeter,
                clientSendStats.operationDestroySendFailedMeter, clientSendStats.operationDestroySendCompletedMeter,
                clientSendStats.operationDestroySendTimerMeter)
    }

    fun endDestroy(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationDestroyInProgressMeter,
                clientStats.operationDestroyFailureCountMeter, clientStats.operationDestroyCountMeter,
                clientStats.operationDestroyTimer, clientStats.operationDestroyTimeoutCountMeter)
    }

    fun startDestroyRegion(): Long = startOperation(clientStats.operationRegionDestroyInProgressMeter, clientSendStats.operationRegionDestroySendInProgressMeter)

    fun endDestroyRegionSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationRegionDestroySendInProgressMeter,
                clientSendStats.operationRegionDestroySendFailedMeter, clientSendStats.operationRegionDestroySendCompletedMeter,
                clientSendStats.operationDestroySendTimerMeter)
    }

    fun endDestroyRegion(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationRegionDestroyInProgressMeter,
                clientStats.operationRegionDestroyFailureCountMeter, clientStats.operationRegionDestroyCountMeter,
                clientStats.operationRegionDestroyTimer, clientStats.operationRegionDestroyTimeoutCountMeter)
    }

    fun startClear(): Long = startOperation(clientStats.operationClearInProgressMeter, clientSendStats.operationClearSendInProgressMeter)

    fun endClearSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationClearSendInProgressMeter,
                clientSendStats.operationClearSendFailedMeter, clientSendStats.operationClearSendCompletedMeter,
                clientSendStats.operationClearSendTimerMeter)
    }

    fun endClear(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationClearInProgressMeter,
                clientStats.operationClearFailureCountMeter, clientStats.operationClearCountMeter,
                clientStats.operationClearTimer, clientStats.operationClearTimeoutCountMeter)
    }

    fun startContainsKey(): Long = startOperation(clientStats.operationContainsKeysInProgressMeter, clientSendStats.operationContainsKeySendInProgressMeter)

    fun endContainsKeySend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationContainsKeySendInProgressMeter,
                clientSendStats.operationContainsKeySendFailedMeter, clientSendStats.operationContainsKeySendCompletedMeter,
                clientSendStats.operationContainsKeySendTimerMeter)
    }

    fun endContainsKey(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationContainsKeysInProgressMeter,
                clientStats.operationContainsKeysFailureCountMeter, clientStats.operationContainsKeysCountMeter,
                clientStats.operationContainsKeysTimer, clientStats.operationContainsKeysTimeoutCountMeter)
    }

    fun startKeySet(): Long = startOperation(clientStats.operationKeySetInProgressMeter, clientSendStats.operationKeySetSendInProgressMeter)

    fun endKeySetSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationKeySetSendInProgressMeter,
                clientSendStats.operationKeySetSendFailedMeter, clientSendStats.operationKeySetSendCompletedMeter,
                clientSendStats.operationKeySetSendTimerMeter)
    }

    fun endKeySet(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationKeySetInProgressMeter,
                clientStats.operationKeySetFailureCountMeter, clientStats.operationKeySetCountMeter,
                clientStats.operationKeySetTimer, clientStats.operationKeySetTimeoutCountMeter)
    }

    fun startRegisterInterest(): Long = startOperation(clientStats.operationRegisterInterestInProgressMeter, clientSendStats.operationRegisterInterestSendInProgressMeter)

    fun endRegisterInterestSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationRegisterInterestSendInProgressMeter,
                clientSendStats.operationRegisterInterestSendFailedMeter, clientSendStats.operationRegisterInterestSendCompletedMeter,
                clientSendStats.operationRegisterInterestSendTimerMeter)
    }

    fun endRegisterInterest(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationRegisterInterestInProgressMeter,
                clientStats.operationRegisterInterestFailureCountMeter, clientStats.operationRegisterInterestCountMeter,
                clientStats.operationRegisterInterestTimer, clientStats.operationRegisterInterestTimeoutCountMeter)
    }

    fun startUnregisterInterest(): Long = startOperation(clientStats.operationUnregisterInterestInProgressMeter, clientSendStats.operationUnregisterInterestSendInProgressMeter)

    fun endUnregisterInterestSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationUnregisterInterestSendInProgressMeter,
                clientSendStats.operationUnregisterInterestSendFailedMeter, clientSendStats.operationUnregisterInterestSendCompletedMeter,
                clientSendStats.operationUnregisterInterestSendTimerMeter)
    }

    fun endUnregisterInterest(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationUnregisterInterestInProgressMeter,
                clientStats.operationUnregisterInterestFailureCountMeter, clientStats.operationUnregisterInterestCountMeter,
                clientStats.operationUnregisterInterestTimer, clientStats.operationUnregisterInterestTimeoutCountMeter)
    }

    fun startQuery(): Long = startOperation(clientStats.operationQueryInProgressMeter, clientSendStats.operationQuerySendInProgressMeter)

    fun endQuerySend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationQuerySendInProgressMeter,
                clientSendStats.operationQuerySendFailedMeter, clientSendStats.operationQuerySendCompletedMeter,
                clientSendStats.operationQuerySendTimerMeter)
    }

    fun endQuery(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationQueryInProgressMeter,
                clientStats.operationQueryFailureCountMeter, clientStats.operationQueryCountMeter,
                clientStats.operationQueryTimer, clientStats.operationQueryTimeoutCountMeter)
    }

    fun startCreateCQ(): Long = startOperation(clientStats.operationCreateCQInProgressMeter, clientSendStats.operationCreateCQSendInProgressMeter)

    fun endCreateCQSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationCreateCQSendInProgressMeter,
                clientSendStats.operationCreateCQSendFailedMeter, clientSendStats.operationCreateCQSendCompletedMeter,
                clientSendStats.operationCreateCQSendTimerMeter)
    }

    fun endCreateCQ(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationCreateCQInProgressMeter,
                clientStats.operationCreateCQFailureCountMeter, clientStats.operationCreateCQCountMeter,
                clientStats.operationCreateCQTimer, clientStats.operationCreateCQTimeoutCountMeter)
    }

    fun startStopCQ(): Long = startOperation(clientStats.operationStopCQInProgressMeter, clientSendStats.operationStopCQSendInProgressMeter)

    fun endStopCQSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationStopCQSendInProgressMeter,
                clientSendStats.operationStopCQSendFailedMeter, clientSendStats.operationStopCQSendCompletedMeter,
                clientSendStats.operationStopCQSendTimerMeter)
    }

    fun endStopCQ(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationStopCQInProgressMeter,
                clientStats.operationStopCQFailureCountMeter, clientStats.operationStopCQCountMeter,
                clientStats.operationStopCQTimer, clientStats.operationStopCQTimeoutCountMeter)
    }

    fun startCloseCQ(): Long = startOperation(clientStats.operationCloseCQInProgressMeter, clientSendStats.operationCloseCQSendInProgressMeter)

    fun endCloseCQSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationCloseCQSendInProgressMeter,
                clientSendStats.operationCloseCQSendFailedMeter, clientSendStats.operationCloseCQSendCompletedMeter,
                clientSendStats.operationCloseCQSendTimerMeter)
    }

    fun endCloseCQ(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationCloseCQInProgressMeter,
                clientStats.operationCloseCQFailureCountMeter, clientStats.operationCloseCQCountMeter,
                clientStats.operationCloseCQTimer, clientStats.operationCloseCQTimeoutCountMeter)
    }

    fun startGetDurableCQs(): Long = startOperation(clientStats.operationGetDurableCQsInProgressMeter, clientSendStats.operationGetDurableCQsSendInProgressMeter)

    fun endGetDurableCQsSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationGetDurableCQsSendInProgressMeter,
                clientSendStats.operationGetDurableCQsSendFailedMeter, clientSendStats.operationGetDurableCQsSendCompletedMeter,
                clientSendStats.operationGetDurableCQsSendTimerMeter)
    }

    fun endGetDurableCQs(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationGetDurableCQsInProgressMeter,
                clientStats.operationGetDurableCQsFailureCountMeter, clientStats.operationGetDurableCQsCountMeter,
                clientStats.operationGetDurableCQsTimer, clientStats.operationGetDurableCQsTimeoutCountMeter)
    }

    fun startGatewayBatch(): Long = startOperation(clientStats.operationGatewayBatchInProgressMeter, clientSendStats.operationGatewayBatchSendInProgressMeter)

    fun endGatewayBatchSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationGatewayBatchSendInProgressMeter,
                clientSendStats.operationGatewayBatchSendFailedMeter, clientSendStats.operationGatewayBatchSendCompletedMeter,
                clientSendStats.operationGatewayBatchSendTimerMeter)
    }

    fun endGatewayBatch(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationGatewayBatchInProgressMeter,
                clientStats.operationGatewayBatchFailureCountMeter, clientStats.operationGatewayBatchCountMeter,
                clientStats.operationGatewayBatchTimer, clientStats.operationGatewayBatchTimeoutCountMeter)
    }

    fun startReadyForEvents(): Long = startOperation(clientStats.operationReadyForEventsInProgressMeter, clientSendStats.operationReadyForEventsSendInProgressMeter)

    fun endReadyForEventsSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationReadyForEventsSendInProgressMeter,
                clientSendStats.operationReadyForEventsSendFailedMeter, clientSendStats.operationReadyForEventsSendCompletedMeter,
                clientSendStats.operationReadyForEventsSendTimerMeter)
    }

    fun endReadyForEvents(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationReadyForEventsInProgressMeter,
                clientStats.operationReadyForEventsFailureCountMeter, clientStats.operationReadyForEventsCountMeter,
                clientStats.operationReadyForEventsTimer, clientStats.operationReadyForEventsTimeoutCountMeter)
    }

    fun startMakePrimary(): Long = startOperation(clientStats.operationMakePrimaryInProgressMeter, clientSendStats.operationMakePrimarySendInProgressMeter)

    fun endMakePrimarySend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationMakePrimarySendInProgressMeter,
                clientSendStats.operationMakePrimarySendFailedMeter, clientSendStats.operationMakePrimarySendCompletedMeter,
                clientSendStats.operationMakePrimarySendTimerMeter)
    }

    fun endMakePrimary(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationMakePrimaryInProgressMeter,
                clientStats.operationMakePrimaryFailureCountMeter, clientStats.operationMakePrimaryCountMeter,
                clientStats.operationMakePrimaryTimer, clientStats.operationMakePrimaryTimeoutCountMeter)
    }

    fun startCloseConnection(): Long = startOperation(clientStats.operationCloseConnectionInProgressMeter, clientSendStats.operationCloseConnectionSendInProgressMeter)

    fun endCloseConnectionSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationCloseConnectionSendInProgressMeter,
                clientSendStats.operationCloseConnectionSendFailedMeter, clientSendStats.operationCloseConnectionSendCompletedMeter,
                clientSendStats.operationCloseConnectionSendTimerMeter)
    }

    fun endCloseConnection(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationCloseConnectionInProgressMeter,
                clientStats.operationCloseConnectionFailureCountMeter, clientStats.operationCloseConnectionCountMeter,
                clientStats.operationCloseConnectionTimer, clientStats.operationCloseConnectionTimeoutCountMeter)
    }

    fun startPrimaryAck(): Long = startOperation(clientStats.operationPrimaryAckInProgressMeter, clientSendStats.operationPrimaryAckSendInProgressMeter)

    fun endPrimaryAckSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationPrimaryAckSendInProgressMeter,
                clientSendStats.operationPrimaryAckSendFailedMeter, clientSendStats.operationPrimaryAckSendCompletedMeter,
                clientSendStats.operationPrimaryAckSendTimerMeter)
    }

    fun endPrimaryAck(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationPrimaryAckInProgressMeter,
                clientStats.operationPrimaryAckFailureCountMeter, clientStats.operationPrimaryAckCountMeter,
                clientStats.operationPrimaryAckTimer, clientStats.operationPrimaryAckTimeoutCountMeter)
    }

    fun startPing(): Long = startOperation(clientStats.operationPingInProgressMeter, clientSendStats.operationPingSendInProgressMeter)

    fun endPingSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationPingSendInProgressMeter,
                clientSendStats.operationPingSendFailedMeter, clientSendStats.operationPingSendCompletedMeter,
                clientSendStats.operationPingSendTimerMeter)
    }

    fun endPing(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationPingInProgressMeter,
                clientStats.operationPingFailureCountMeter, clientStats.operationPingCountMeter,
                clientStats.operationPingTimer, clientStats.operationPingTimeoutCountMeter)
    }

    fun startRegisterInstantiators(): Long = startOperation(clientStats.operationRegisterInstantiatorsInProgressMeter, clientSendStats.operationRegisterInstantiatorsSendInProgressMeter)

    fun endRegisterInstantiatorsSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationRegisterInstantiatorsSendInProgressMeter,
                clientSendStats.operationRegisterInstantiatorsSendFailedMeter, clientSendStats.operationRegisterInstantiatorsSendCompletedMeter,
                clientSendStats.operationRegisterInstantiatorsSendTimerMeter)
    }

    fun endRegisterInstantiators(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationRegisterInstantiatorsInProgressMeter,
                clientStats.operationRegisterInstantiatorsFailureCountMeter, clientStats.operationRegisterInstantiatorsCountMeter,
                clientStats.operationRegisterInstantiatorsTimer, clientStats.operationRegisterInstantiatorsTimeoutCountMeter)
    }

    fun startRegisterDataSerializers(): Long = startOperation(clientStats.operationRegisterDataSerializersInProgressMeter, clientSendStats.operationRegisterDataSerializersSendInProgressMeter)

    fun endRegisterDataSerializersSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationRegisterDataSerializersSendInProgressMeter,
                clientSendStats.operationRegisterDataSerializersSendFailedMeter, clientSendStats.operationRegisterDataSerializersSendCompletedMeter,
                clientSendStats.operationRegisterDataSerializersSendTimerMeter)
    }

    fun endRegisterDataSerializers(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationRegisterDataSerializersInProgressMeter,
                clientStats.operationRegisterDataSerializersFailureCountMeter, clientStats.operationRegisterDataSerializersCountMeter,
                clientStats.operationRegisterDataSerializersTimer, clientStats.operationRegisterDataSerializersTimeoutCountMeter)
    }

    fun startPutAll(): Long = startOperation(clientStats.operationPutAllInProgressMeter, clientSendStats.operationPutAllSendInProgressMeter)

    fun endPutAllSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationPutAllSendInProgressMeter,
                clientSendStats.operationPutAllSendFailedMeter, clientSendStats.operationPutAllSendCompletedMeter,
                clientSendStats.operationPutAllSendTimerMeter)
    }

    fun endPutAll(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationPutAllInProgressMeter,
                clientStats.operationPutAllFailureCountMeter, clientStats.operationPutAllCountMeter,
                clientStats.operationPutAllTimer, clientStats.operationPutAllTimeoutCountMeter)
    }

    fun startRemoveAll(): Long = startOperation(clientStats.operationRemoveAllInProgressMeter, clientSendStats.operationRemoveAllSendInProgressMeter)

    fun endRemoveAllSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationRemoveAllSendInProgressMeter,
                clientSendStats.operationRemoveAllSendFailedMeter, clientSendStats.operationRemoveAllSendCompletedMeter,
                clientSendStats.operationRemoveAllSendTimerMeter)
    }

    fun endRemoveAll(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationRemoveAllInProgressMeter,
                clientStats.operationRemoveAllFailureCountMeter, clientStats.operationRemoveAllCountMeter,
                clientStats.operationRemoveAllTimer, clientStats.operationRemoveAllTimeoutCountMeter)
    }

    fun startGetAll(): Long = startOperation(clientStats.operationGetAllInProgressMeter, clientSendStats.operationGetAllSendInProgressMeter)

    fun endGetAllSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationGetAllSendInProgressMeter,
                clientSendStats.operationGetAllSendFailedMeter, clientSendStats.operationGetAllSendCompletedMeter,
                clientSendStats.operationGetAllSendTimerMeter)
    }

    fun endGetAll(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationGetAllInProgressMeter,
                clientStats.operationGetAllFailureCountMeter, clientStats.operationGetAllCountMeter,
                clientStats.operationGetAllTimer, clientStats.operationGetAllTimeoutCountMeter)
    }


    fun incConnections(value: Int) {
        this.clientStats.connectionsCurrentMeter.increment(value.toDouble())
        if (value > 0) {
            this.clientStats.connectionsCreateMeter.increment(value.toDouble())
        } else if (value < 0) {
            this.clientStats.connectionsDisconnectMeter.increment(value.toDouble())
        }
        this.poolStats.incConnections(value)
    }

    private fun startClientOp() {
        this.poolStats.startClientOp()
    }

    private fun endClientOpSend(duration: Long, failed: Boolean) {
        this.poolStats.endClientOpSend(duration, failed)
    }

    private fun endClientOp(duration: Long, timedOut: Boolean, failed: Boolean) {
        this.poolStats.endClientOp(duration, timedOut, failed)
    }

    override fun incReceivedBytes(value: Long) {
        this.clientStats.receivedBytesMeter.increment(value.toDouble())
    }

    override fun incSentBytes(value: Long) {
        this.clientStats.sentBytesMeter.increment(value.toDouble())
    }

    override fun incMessagesBeingReceived(bytes: Int) {
        clientStats.messagesReceivedMeter.increment()
        if (bytes > 0) {
            clientStats.messagesReceivedBytesMeter.increment(bytes.toDouble())
        }
    }

    fun startExecuteFunction(): Long = startOperation(clientStats.operationExecuteFunctionInProgressMeter, clientSendStats.operationExecuteFunctionSendInProgressMeter)

    fun endExecuteFunctionSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationExecuteFunctionSendInProgressMeter,
                clientSendStats.operationExecuteFunctionSendFailedMeter, clientSendStats.operationExecuteFunctionSendCompletedMeter,
                clientSendStats.operationExecuteFunctionSendTimerMeter)
    }

    fun endExecuteFunction(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationExecuteFunctionInProgressMeter,
                clientStats.operationExecuteFunctionFailureCountMeter, clientStats.operationExecuteFunctionCountMeter,
                clientStats.operationExecuteFunctionTimer, clientStats.operationExecuteFunctionTimeoutCountMeter)
    }

    fun startGetClientPRMetadata(): Long = startOperation(clientStats.operationGetClientPRMetadataInProgressMeter, clientSendStats.operationGetClientPRMetadataSendInProgressMeter)

    fun endGetClientPRMetadataSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationGetClientPRMetadataSendInProgressMeter,
                clientSendStats.operationGetClientPRMetadataSendFailedMeter, clientSendStats.operationGetClientPRMetadataSendCompletedMeter,
                clientSendStats.operationGetClientPRMetadataSendTimerMeter)
    }

    fun endGetClientPRMetadata(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationGetClientPRMetadataInProgressMeter,
                clientStats.operationGetClientPRMetadataFailureCountMeter, clientStats.operationGetClientPRMetadataCountMeter,
                clientStats.operationGetClientPRMetadataTimer, clientStats.operationGetClientPRMetadataTimeoutCountMeter)
    }

    fun startGetClientPartitionAttributes(): Long = startOperation(clientStats.operationGetClientPartitionAttributesInProgressMeter, clientSendStats.operationGetClientPartitionAttributesSendInProgressMeter)

    fun endGetClientPartitionAttributesSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationGetClientPartitionAttributesSendInProgressMeter,
                clientSendStats.operationGetClientPartitionAttributesSendFailedMeter, clientSendStats.operationGetClientPartitionAttributesSendCompletedMeter,
                clientSendStats.operationGetClientPartitionAttributesSendTimerMeter)
    }

    fun endGetClientPartitionAttributes(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationGetClientPartitionAttributesInProgressMeter,
                clientStats.operationGetClientPartitionAttributesFailureCountMeter, clientStats.operationGetClientPartitionAttributesCountMeter,
                clientStats.operationGetClientPartitionAttributesTimer, clientStats.operationGetClientPartitionAttributesTimeoutCountMeter)
    }

    fun startGetPDXTypeById(): Long = startOperation(clientStats.operationGetPDXTypeByIdInProgressMeter, clientSendStats.operationGetPDXTypeByIdSendInProgressMeter)

    fun endGetPDXTypeByIdSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationGetPDXTypeByIdSendInProgressMeter,
                clientSendStats.operationGetPDXTypeByIdSendFailedMeter, clientSendStats.operationGetPDXTypeByIdSendCompletedMeter,
                clientSendStats.operationGetPDXTypeByIdSendTimerMeter)
    }

    fun endGetPDXTypeById(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationGetPDXTypeByIdInProgressMeter,
                clientStats.operationGetPDXTypeByIdFailureCountMeter, clientStats.operationGetPDXTypeByIdCountMeter,
                clientStats.operationGetPDXTypeByIdTimer, clientStats.operationGetPDXTypeByIdTimeoutCountMeter)
    }

    fun startGetPDXIdForType(): Long = startOperation(clientStats.operationGetPDXIdForTypeInProgressMeter, clientSendStats.operationGetPDXIdForTypeSendInProgressMeter)

    fun endGetPDXIdForTypeSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationGetPDXIdForTypeSendInProgressMeter,
                clientSendStats.operationGetPDXIdForTypeSendFailedMeter, clientSendStats.operationGetPDXIdForTypeSendCompletedMeter,
                clientSendStats.operationGetPDXIdForTypeSendTimerMeter)
    }

    fun endGetPDXIdForType(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationGetPDXIdForTypeInProgressMeter,
                clientStats.operationGetPDXIdForTypeFailureCountMeter, clientStats.operationGetPDXIdForTypeCountMeter,
                clientStats.operationGetPDXIdForTypeTimer, clientStats.operationGetPDXIdForTypeTimeoutCountMeter)
    }

    fun startAddPdxType(): Long = startOperation(clientStats.operationAddPdxTypeInProgressMeter, clientSendStats.operationAddPdxTypeSendInProgressMeter)

    fun endAddPdxTypeSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationAddPdxTypeSendInProgressMeter,
                clientSendStats.operationAddPdxTypeSendFailedMeter, clientSendStats.operationAddPdxTypeSendCompletedMeter,
                clientSendStats.operationAddPdxTypeSendTimerMeter)
    }

    fun endAddPdxType(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationAddPdxTypeInProgressMeter,
                clientStats.operationAddPdxTypeFailureCountMeter, clientStats.operationAddPdxTypeCountMeter,
                clientStats.operationAddPdxTypeTimer, clientStats.operationAddPdxTypeTimeoutCountMeter)
    }

    fun startSize(): Long = startOperation(clientStats.operationSizeInProgressMeter, clientSendStats.operationSizeSendInProgressMeter)

    fun endSizeSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationSizeSendInProgressMeter,
                clientSendStats.operationSizeSendFailedMeter, clientSendStats.operationSizeSendCompletedMeter,
                clientSendStats.operationSizeSendTimerMeter)
    }

    fun endSize(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationSizeInProgressMeter,
                clientStats.operationSizeFailureCountMeter, clientStats.operationSizeCountMeter,
                clientStats.operationSizeTimer, clientStats.operationSizeTimeoutCountMeter)
    }

    fun startInvalidate(): Long = startOperation(clientStats.operationInvalidateInProgressMeter, clientSendStats.operationInvalidateSendInProgressMeter)

    fun endInvalidateSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationInvalidateSendInProgressMeter,
                clientSendStats.operationInvalidateSendFailedMeter, clientSendStats.operationInvalidateSendCompletedMeter,
                clientSendStats.operationInvalidateSendTimerMeter)
    }

    fun endInvalidate(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationInvalidateInProgressMeter,
                clientStats.operationInvalidateFailureCountMeter, clientStats.operationInvalidateCountMeter,
                clientStats.operationInvalidateTimer, clientStats.operationInvalidateTimeoutCountMeter)
    }

    fun startCommit(): Long = startOperation(clientStats.operationCommitInProgressMeter, clientSendStats.operationCommitSendInProgressMeter)

    fun endCommitSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationCommitSendInProgressMeter,
                clientSendStats.operationCommitSendFailedMeter, clientSendStats.operationCommitSendCompletedMeter,
                clientSendStats.operationCommitSendTimerMeter)
    }

    fun endCommit(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationCommitInProgressMeter,
                clientStats.operationCommitFailureCountMeter, clientStats.operationCommitCountMeter,
                clientStats.operationCommitTimer, clientStats.operationCommitTimeoutCountMeter)
    }

    fun startGetEntry(): Long = startOperation(clientStats.operationGetEntryInProgressMeter, clientSendStats.operationGetEntrySendInProgressMeter)

    fun endGetEntrySend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationGetEntrySendInProgressMeter,
                clientSendStats.operationGetEntrySendFailedMeter, clientSendStats.operationGetEntrySendCompletedMeter,
                clientSendStats.operationGetEntrySendTimerMeter)
    }

    fun endGetEntry(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationGetEntryInProgressMeter,
                clientStats.operationGetEntryFailureCountMeter, clientStats.operationGetEntryCountMeter,
                clientStats.operationGetEntryTimer, clientStats.operationGetEntryTimeoutCountMeter)
    }

    fun startRollback(): Long = startOperation(clientStats.operationRollbackInProgressMeter, clientSendStats.operationRollbackSendInProgressMeter)

    fun endRollbackSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationRollbackSendInProgressMeter,
                clientSendStats.operationRollbackSendFailedMeter, clientSendStats.operationRollbackSendCompletedMeter,
                clientSendStats.operationRollbackSendTimerMeter)
    }

    fun endRollback(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationRollbackInProgressMeter,
                clientStats.operationRollbackFailureCountMeter, clientStats.operationRollbackCountMeter,
                clientStats.operationRollbackTimer, clientStats.operationRollbackTimeoutCountMeter)
    }

    fun startTxFailover(): Long = startOperation(clientStats.operationTxFailoverInProgressMeter, clientSendStats.operationTxFailoverSendInProgressMeter)

    fun endTxFailoverSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationTxFailoverSendInProgressMeter,
                clientSendStats.operationTxFailoverSendFailedMeter, clientSendStats.operationTxFailoverSendCompletedMeter,
                clientSendStats.operationTxFailoverSendTimerMeter)
    }

    fun endTxFailover(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationTxFailoverInProgressMeter,
                clientStats.operationTxFailoverFailureCountMeter, clientStats.operationTxFailoverCountMeter,
                clientStats.operationTxFailoverTimer, clientStats.operationTxFailoverTimeoutCountMeter)
    }

    fun startTxSynchronization(): Long = startOperation(clientStats.operationTxSynchronizationInProgressMeter, clientSendStats.operationTxSynchronizationSendInProgressMeter)

    fun endTxSynchronizationSend(startTime: Long, failed: Boolean) {
        endOperationSendStats(startTime, failed, clientSendStats.operationTxSynchronizationSendInProgressMeter,
                clientSendStats.operationTxSynchronizationSendFailedMeter, clientSendStats.operationTxSynchronizationSendCompletedMeter,
                clientSendStats.operationTxSynchronizationSendTimerMeter)
    }

    fun endTxSynchronization(startTime: Long, timedOut: Boolean, failed: Boolean) {
        endOperationStats(startTime, timedOut, failed, clientStats.operationTxSynchronizationInProgressMeter,
                clientStats.operationTxSynchronizationFailureCountMeter, clientStats.operationTxSynchronizationCountMeter,
                clientStats.operationTxSynchronizationTimer, clientStats.operationTxSynchronizationTimeoutCountMeter)
    }
}