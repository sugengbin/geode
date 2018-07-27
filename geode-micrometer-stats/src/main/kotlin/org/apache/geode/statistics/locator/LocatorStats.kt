package org.apache.geode.statistics.locator

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter
import org.apache.geode.statistics.util.NOW_NANOS

class LocatorStats(locatorName: String) : MicrometerMeterGroup("LocatorStats-$locatorName") {
    override fun initializeStaticMeters() {
        registerMeter(locatorKnownMeter)
        registerMeter(locatorClientRequestMeter)
        registerMeter(locatorClientResponseMeter)
        registerMeter(locatorKnownServerMeter)
        registerMeter(locatorRequestInProgressMeter)
        registerMeter(locatorClientRequestTimer)
        registerMeter(locatorClientResponseTimer)
        registerMeter(locatorServerLoadUpdateMeter)
    }

    private val locatorKnownMeter = GaugeStatisticMeter("locator.count", "Number of locators known to this locator")
    private val locatorClientRequestMeter = CounterStatisticMeter("locator.client.request", "Number of requests this locator has received from clients")
    private val locatorClientResponseMeter = CounterStatisticMeter("locator.client.response", "Number of responses this locator has sent to clients")
    private val locatorKnownServerMeter = GaugeStatisticMeter("locator.server.count", "Number of servers this locator knows about")
    private val locatorRequestInProgressMeter = GaugeStatisticMeter("locator.request.inprogress", "The number of location requests currently being processed by the thread pool.")
    private val locatorClientRequestTimer = TimerStatisticMeter("locator.request.time", "Time spent processing server location requests", unit = "nanoseconds")
    private val locatorClientResponseTimer = TimerStatisticMeter("locator.response.time", "Time spent sending location responses to clients", unit = "nanoseconds")
    private val locatorServerLoadUpdateMeter = CounterStatisticMeter("locator.server.load.update.count", "Total number of times a server load update has been received.")


    fun setServerCount(serverCount: Int) {
        locatorKnownServerMeter.setValue(serverCount)
    }

    fun setLocatorCount(locatorCount: Int) {
        locatorKnownMeter.setValue(locatorCount)
    }

    fun endLocatorRequest(startTime: Long) {
        locatorClientRequestMeter.increment()
        locatorClientRequestTimer.recordValue(NOW_NANOS - startTime)
    }

    fun endLocatorResponse(startTime: Long) {
        locatorClientResponseMeter.increment()
        locatorClientResponseTimer.recordValue(NOW_NANOS - startTime)
    }


    fun setLocatorRequests(locatorRequests: Long) {
        locatorClientRequestMeter.increment(locatorRequests)
    }

    fun setLocatorResponses(locatorResponses: Long) {
        locatorClientResponseMeter.increment(locatorResponses)
    }

    fun setServerLoadUpdates(serverLoadUpdates: Long) {
        locatorServerLoadUpdateMeter.increment(serverLoadUpdates)
    }

    fun incServerLoadUpdates() {
        locatorServerLoadUpdateMeter.increment()
    }

    fun incRequestInProgress(threads: Int) {
        locatorRequestInProgressMeter.increment(threads)
    }
}