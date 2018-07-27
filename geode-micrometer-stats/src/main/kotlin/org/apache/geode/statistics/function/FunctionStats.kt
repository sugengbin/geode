package org.apache.geode.statistics.function

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter
import java.util.concurrent.ConcurrentHashMap

class FunctionStats(functionName: String) : MicrometerMeterGroup("FunctionStats") {
    //This is a necessary evil for now. Until we can work out how the stats stuff really fits into
    //the new modular world.
    companion object {
        private val functionExecutionStatsMap = ConcurrentHashMap<String, FunctionStats>()

        @JvmStatic
        fun getFunctionStats(textId: String): FunctionStats =
                functionExecutionStatsMap[textId] ?: run {
                    val functionStats = FunctionStats(textId)
                    functionExecutionStatsMap[textId] = functionStats
                    functionStats
                }

    }

    private val functionExecutionsCompletedMeter = CounterStatisticMeter("function.execution",
            "Total number of completed function.execute() calls for given function", arrayOf("functionId", functionName, "status", "completed"))
    private val functionExecutionTimer = TimerStatisticMeter("function.execution.timer",
            "Total time consumed for all completed invocations of the given function", arrayOf("functionId", functionName, "status", "completed"), "nanoseconds")
    private val functionExecutionsInProgressMeter = GaugeStatisticMeter("function.execution.inprogress",
            "number of currently running invocations of the given function", arrayOf("functionId", functionName))
    private val functionExecutionResultsSentToCollectorMeter = CounterStatisticMeter("function,execution.results",
            "Total number of results sent to the ResultCollector", arrayOf("functionId", functionName, "results", "sent"))
    private val functionExecutionResultsSendReceiveByCollectorMeter = CounterStatisticMeter("function,execution.results",
            "Total number of results received and passed to the ResultCollector", arrayOf("functionId", functionName, "results", "sent-receive"))
    private val functionExecutionsMeter = CounterStatisticMeter("function.execution",
            "Total number of FunctionService.execute() calls for given function", arrayOf("functionId", functionName))
    private val functionExecutionWithResultTimer = TimerStatisticMeter("function.execution.timer",
            "Total time consumed for all completed given function.execute() calls where hasResult() returns true.",
            arrayOf("functionId", functionName, "withResult", "true", "status", "completed"), "nanoseconds")
    private val functionExecutionsInProgressWithResultMeter = GaugeStatisticMeter("function.execution.inprogress",
            "A gauge indicating the number of currently active execute() calls for functions where hasResult() returns true.",
            arrayOf("functionId", functionName, "withResult", "true"))
    private val functionExecutionsExceptionMeter = CounterStatisticMeter("function.execution",
            "Total number of Exceptions Occurred while executing function", arrayOf("functionId", functionName, "status", "exception"))

    override fun initializeStaticMeters() {
        registerMeter(functionExecutionsCompletedMeter)
        registerMeter(functionExecutionTimer)
        registerMeter(functionExecutionsInProgressMeter)
        registerMeter(functionExecutionResultsSentToCollectorMeter)
        registerMeter(functionExecutionResultsSendReceiveByCollectorMeter)
        registerMeter(functionExecutionsMeter)
        registerMeter(functionExecutionWithResultTimer)
        registerMeter(functionExecutionsInProgressWithResultMeter)
        registerMeter(functionExecutionsExceptionMeter)
    }

    fun incFunctionExecutionsCompleted() {
        functionExecutionsCompletedMeter.increment()
    }

    fun incFunctionExecutionsRunning() {
        functionExecutionsInProgressMeter.increment()
    }

    fun incResultsReturned() {
        functionExecutionResultsSentToCollectorMeter.increment()
    }

    fun incResultsReceived() {
        functionExecutionResultsSendReceiveByCollectorMeter.increment()
    }

    fun incFunctionExecutionCalls() {
        functionExecutionsMeter.increment()
    }

    fun incFunctionExecutionHasResultRunning() {
        functionExecutionsInProgressWithResultMeter.increment()
    }

    fun incFunctionExecutionExceptions() {
        functionExecutionsExceptionMeter.increment()
    }

    fun startFunctionExecution(haveResult: Boolean) {
        incFunctionExecutionCalls()
        if (haveResult) {
            incFunctionExecutionHasResultRunning()
        } else {
            incFunctionExecutionsRunning()
        }
    }


    fun endFunctionExecution(start: Long, haveResult: Boolean) {
        val elapsed = System.nanoTime() - start
        incFunctionExecutionsCompleted()

        if (haveResult) {
            functionExecutionsInProgressWithResultMeter.decrement()
            functionExecutionWithResultTimer.recordValue(elapsed)
        }
        else{
            functionExecutionsInProgressMeter.decrement()
            functionExecutionTimer.recordValue(elapsed)
        }
    }

    fun endFunctionExecutionWithException(haveResult: Boolean) {
        functionExecutionsExceptionMeter.increment()

        if (haveResult) {
            functionExecutionsInProgressWithResultMeter.decrement()
        }else{
            functionExecutionsInProgressMeter.decrement()
        }
    }
}