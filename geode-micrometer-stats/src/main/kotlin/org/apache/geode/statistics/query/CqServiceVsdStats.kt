package org.apache.geode.statistics.query

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter
import org.apache.geode.statistics.util.NOW_NANOS

class CqServiceVsdStats : MicrometerMeterGroup("CqServiceStats") {

    private val cqNumberCQsCreatedMeter = GaugeStatisticMeter("cq.count", "Number of CQs created")
    private val cqNumberCQsActiveMeter = GaugeStatisticMeter("cq.count", "Number of CQS actively executing.", arrayOf("state", "active"))
    private val cqNumberCQsStoppedMeter = GaugeStatisticMeter("cq.count", "Number of CQs stopped.", arrayOf("state", "stopped"))
    private val cqNumberCQsClosedMeter = GaugeStatisticMeter("cq.count", "Number of CQs closed.", arrayOf("state", "closed"))
    private val cqNumberCQsDefinedOnClient = GaugeStatisticMeter("cq.count.client", "Number of CQs on the client.")
    private val cqNumberOfClientsWithCQDefinedMeter = GaugeStatisticMeter("cq.client.count", "Number of Clients with CQs.")
    private val cqQueryExecutionTimer = TimerStatisticMeter("cq.query.execution.time", "Time taken for CQ Query Execution.", unit = "nanoseconds")
    private val cqQueryExecutionCompletedMeter = CounterStatisticMeter("cq.query.execution.completed.count", "Number of CQ Query Executions.")
    private val cqQueryExecutionInProgressMeter = GaugeStatisticMeter("cq.query.execution.inprogress.count", "CQ Query Execution In Progress.")
    private val cqUniqueQueriesMeter = GaugeStatisticMeter("cq.query.unique.count", "Number of Unique CQ Querys.")

    override fun initializeStaticMeters() {
        registerMeter(cqNumberCQsCreatedMeter)
        registerMeter(cqNumberCQsActiveMeter)
        registerMeter(cqNumberCQsStoppedMeter)
        registerMeter(cqNumberCQsClosedMeter)
        registerMeter(cqNumberOfClientsWithCQDefinedMeter)
        registerMeter(cqQueryExecutionTimer)
        registerMeter(cqQueryExecutionCompletedMeter)
        registerMeter(cqQueryExecutionInProgressMeter)
        registerMeter(cqUniqueQueriesMeter)
    }


    fun incCqsCreated() {
        cqNumberCQsCreatedMeter.increment()
    }

    fun incCqsActive() {
        cqNumberCQsActiveMeter.increment()
    }

    fun decCqsActive() {
        cqNumberCQsActiveMeter.decrement()
    }

    fun incCqsStopped() {
        cqNumberCQsStoppedMeter.increment()
    }

    fun decCqsStopped() {
        cqNumberCQsStoppedMeter.decrement()
    }

    fun incCqsClosed() {
        cqNumberCQsClosedMeter.increment()
    }

    fun incCqsOnClient() {
        cqNumberCQsDefinedOnClient.increment()
    }

    fun decCqsOnClient() {
        cqNumberCQsDefinedOnClient.decrement()
    }

    fun incClientsWithCqs() {
        cqNumberOfClientsWithCQDefinedMeter.increment()
    }

    fun decClientsWithCqs() {
        cqNumberOfClientsWithCQDefinedMeter.decrement()
    }

    fun startCqQueryExecution(): Long {
        cqQueryExecutionInProgressMeter.increment()
        return NOW_NANOS
    }

    fun endCqQueryExecution(start: Long) {
        cqQueryExecutionTimer.recordValue(NOW_NANOS - start)
        cqQueryExecutionInProgressMeter.decrement()
        cqQueryExecutionCompletedMeter.increment()
    }

    fun incUniqueCqQuery() {
        cqUniqueQueriesMeter.increment()
    }

    fun decUniqueCqQuery() {
        cqUniqueQueriesMeter.decrement()
    }
}