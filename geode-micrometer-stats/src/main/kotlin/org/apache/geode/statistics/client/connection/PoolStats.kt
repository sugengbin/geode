package org.apache.geode.statistics.client.connection

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter

class PoolStats(val poolName: String) : MicrometerMeterGroup("PoolStats-$poolName") {

    private val initialContactsMeter = GaugeStatisticMeter("pool.initial.contacts", "Number of contacts initially by user", arrayOf("poolName", poolName))
    private val locatorsDiscoveredMeter = GaugeStatisticMeter("pool.locators.discovered", "Current number of locators discovered", arrayOf("poolName", poolName))
    private val serversDiscoveredMeter = GaugeStatisticMeter("pool.servers.discovered", "Current number of servers discovered", arrayOf("poolName", poolName))
    private val subscriptionServersMeter = GaugeStatisticMeter("pool.servers.subscription", "Number of servers hosting this clients subscriptions", arrayOf("poolName", poolName))
    private val requestsToLocatorMeter = CounterStatisticMeter("pool.locator.requests", "Number of requests from this connection pool to a locator", arrayOf("poolName", poolName))
    private val responsesFromLocatorMeter = CounterStatisticMeter("pool.locator.responses", "Number of responses received by pool from locator", arrayOf("poolName", poolName))
    private val currentConnectionCountMeter = GaugeStatisticMeter("pool.connection.count", "Current number of connections", arrayOf("poolName", poolName))
    private val currentPoolConnectionCountMeter = GaugeStatisticMeter("pool.connection.pooled.count", "Current number of pool connections", arrayOf("poolName", poolName))
    private val connectionCreateMeter = CounterStatisticMeter("pool.connection.create", "Total number of times a connection has been created.", arrayOf("poolName", poolName))
    private val connectionDisconnectMeter = CounterStatisticMeter("pool.connection.disconnect", "Total number of times a connection has been destroyed.", arrayOf("poolName", poolName))
    private val minPoolConnectCountMeter = CounterStatisticMeter("pool.connection.create.min", "Total number of connects done to maintain minimum pool size.", arrayOf("poolName", poolName))
    private val loadConditioningConnectCountMeter = CounterStatisticMeter("pool.connection.create.loadconditioning", "Total number of connects done due to load conditioning.", arrayOf("poolName", poolName))
    private val loadConditioningReplaceCountMeter = CounterStatisticMeter("pool.connection.loadconditioning.replace", "Total number of times a load conditioning connect was done but was not used.", arrayOf("poolName", poolName))
    private val idleConnectionDisconnectCountMeter = CounterStatisticMeter("pool.connection.disconnect.idle", "Total number of disconnects done due to idle expiration.", arrayOf("poolName", poolName))
    private val loadConditioningDisconnectCountMeter = CounterStatisticMeter("pool.connection.disconnect.loadconditioning", "Total number of disconnects done due to load conditioning expiration.", arrayOf("poolName", poolName))
    private val idleConnectionCheckCountMeter = CounterStatisticMeter("pool.connection.check.idle", "Total number of checks done for idle expiration.", arrayOf("poolName", poolName))
    private val loadConditioningCheckCountMeter = CounterStatisticMeter("pool.connection.check.loadconditioning", "Total number of checks done for load conditioning expiration.", arrayOf("poolName", poolName))
    private val loadConditioningExtensionCountMeter = CounterStatisticMeter("pool.connection.extension.loadconditioning", "Total number of times a connection's load conditioning has been extended because the servers are still balanced.", arrayOf("poolName", poolName))
    private val connectionWaitInProgressMeter = GaugeStatisticMeter("pool.connection.wait.inprogress", "Current number of threads waiting for a connection", arrayOf("poolName", poolName))
    private val connectionWaitMeter = CounterStatisticMeter("pool.connection.wait.count", "Total number of times a thread completed waiting for a connection (by timing out or by getting a connection).", arrayOf("poolName", poolName))
    private val connectionWaitTimeMeter = TimerStatisticMeter("pool.connection.wait.time", "Total number of nanoseconds spent waiting for a connection.", arrayOf("poolName", poolName), unit = "nanoseconds")
    private val clientOpsInProgressMeter = GaugeStatisticMeter("pool.connection.ops.inprogress", "Current number of clientOps being executed", arrayOf("poolName", poolName))
    private val clientOpSendsInProgressMeter = GaugeStatisticMeter("pool.connection.ops.sends.inprogress", "Current number of clientOp sends being executed", arrayOf("poolName", poolName))
    private val clientOpSendsCountMeter = CounterStatisticMeter("pool.connection.ops.sends.count", "Total number of clientOp sends that have completed successfully", arrayOf("poolName", poolName))
    private val clientOpSendsFailuresMeter = CounterStatisticMeter("pool.connection.ops.sends.count", "Total number of clientOp sends that have failed", arrayOf("poolName", poolName))
    private val clientOpSuccessMeter = CounterStatisticMeter("pool.connection.ops.success", "Total number of clientOps completed successfully", arrayOf("poolName", poolName))
    private val clientOpFailureMeter = CounterStatisticMeter("pool.connection.ops.failure", "Total number of clientOp attempts that have failed", arrayOf("poolName", poolName))
    private val clientOpTimeoutMeter = CounterStatisticMeter("pool.connection.ops.timeout", "Total number of clientOp attempts that have timed out", arrayOf("poolName", poolName))
    private val clientOpSendTimeMeter = TimerStatisticMeter("pool.connection.ops.sends.time", "Total amount of time, in nanoseconds spent doing clientOp sends", arrayOf("poolName", poolName), unit = "nanoseconds")
    private val clientOpTimeMeter = TimerStatisticMeter("pool.connection.ops.time", "Total amount of time, in nanoseconds spent doing clientOps", arrayOf("poolName", poolName), unit = "nanoseconds")

    override fun initializeStaticMeters() {
        registerMeter(initialContactsMeter)
        registerMeter(locatorsDiscoveredMeter)
        registerMeter(serversDiscoveredMeter)
        registerMeter(subscriptionServersMeter)
        registerMeter(requestsToLocatorMeter)
        registerMeter(responsesFromLocatorMeter)
        registerMeter(currentConnectionCountMeter)
        registerMeter(currentPoolConnectionCountMeter)
        registerMeter(connectionCreateMeter)
        registerMeter(connectionDisconnectMeter)
        registerMeter(minPoolConnectCountMeter)
        registerMeter(loadConditioningConnectCountMeter)
        registerMeter(loadConditioningReplaceCountMeter)
        registerMeter(idleConnectionDisconnectCountMeter)
        registerMeter(loadConditioningDisconnectCountMeter)
        registerMeter(idleConnectionCheckCountMeter)
        registerMeter(loadConditioningCheckCountMeter)
        registerMeter(loadConditioningExtensionCountMeter)
        registerMeter(connectionWaitInProgressMeter)
        registerMeter(connectionWaitMeter)
        registerMeter(connectionWaitTimeMeter)
        registerMeter(clientOpsInProgressMeter)
        registerMeter(clientOpSendsInProgressMeter)
        registerMeter(clientOpSendsCountMeter)
        registerMeter(clientOpSendsFailuresMeter)
        registerMeter(clientOpSuccessMeter)
        registerMeter(clientOpFailureMeter)
        registerMeter(clientOpTimeoutMeter)
        registerMeter(clientOpSendTimeMeter)
        registerMeter(clientOpTimeMeter)
    }

    fun setInitialContacts(value: Int) {
        initialContactsMeter.setValue(value.toDouble())
    }

    fun setServerCount(value: Int) {
        serversDiscoveredMeter.setValue(value.toDouble())
    }

    fun setSubscriptionCount(value: Int) {
        subscriptionServersMeter.setValue(value.toDouble())
    }

    fun setLocatorCount(value: Int) {
        locatorsDiscoveredMeter.setValue(value.toDouble())
    }

    fun incLocatorRequests() {
        requestsToLocatorMeter.increment()
    }

    fun incLocatorResponses() {
        responsesFromLocatorMeter.increment()
    }

    fun setLocatorRequests(value: Long) {
        requestsToLocatorMeter.increment(value.toDouble())
    }

    fun setLocatorResponses(value: Long) {
        responsesFromLocatorMeter.increment(value.toDouble())
    }

    fun incConnections(value: Int) {
        currentConnectionCountMeter.increment(value.toDouble())
        if (value > 0) {
            connectionCreateMeter.increment(value.toDouble())
        } else if (value < 0) {
            connectionDisconnectMeter.increment(value.toDouble())
        }
    }

    fun incPoolConnections(value: Int) {
        currentPoolConnectionCountMeter.increment(value.toDouble())
    }

    fun incPrefillConnect() {
        minPoolConnectCountMeter.increment()
    }

    fun incLoadConditioningCheck() {
        loadConditioningCheckCountMeter.increment()
    }

    fun incLoadConditioningExtensions() {
        loadConditioningExtensionCountMeter.increment()
    }

    fun incIdleCheck() {
        idleConnectionCheckCountMeter.increment()
    }

    fun incLoadConditioningConnect() {
        loadConditioningConnectCountMeter.increment()
    }

    fun incLoadConditioningReplaceTimeouts() {
        loadConditioningReplaceCountMeter.increment()
    }

    fun incLoadConditioningDisconnect() {
        loadConditioningDisconnectCountMeter.increment()
    }

    fun incIdleExpire(value: Int) {
        idleConnectionDisconnectCountMeter.increment(value.toDouble())
    }

    fun beginConnectionWait(): Long {
        connectionWaitInProgressMeter.increment()
        return System.nanoTime()
    }

    fun endConnectionWait(start: Long) {
        val duration = System.nanoTime() - start
        connectionWaitInProgressMeter.decrement()
        connectionWaitMeter.increment()
        connectionWaitTimeMeter.recordValue(duration)
    }

    fun startClientOp() {
        clientOpsInProgressMeter.increment()
        clientOpSendsInProgressMeter.increment()
    }

    fun endClientOpSend(duration: Long, failed: Boolean) {
        clientOpSendsInProgressMeter.decrement()
        if (failed) {
            clientOpSendsFailuresMeter.increment()
        } else {
            clientOpSendsCountMeter.increment()
        }
        clientOpSendTimeMeter.recordValue(duration)
    }

    fun endClientOp(duration: Long, timedOut: Boolean, failed: Boolean) {
        clientOpsInProgressMeter.decrement()
        when {
            timedOut -> clientOpTimeoutMeter.increment()
            failed -> clientOpFailureMeter.increment()
            else -> clientOpSuccessMeter.increment()
        }
        clientOpTimeMeter.recordValue(duration)
    }
}