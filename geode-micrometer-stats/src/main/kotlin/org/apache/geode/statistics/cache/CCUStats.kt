package org.apache.geode.statistics.cache

import org.apache.geode.internal.cache.tier.sockets.MessageStats
import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup

/**
 * Stats for a CacheClientUpdater. Currently the only thing measured are incoming bytes on the
 * wire
 *
 * @since GemFire 5.7
 */
class CCUStats internal constructor(private val serverLocation: String) : MicrometerMeterGroup("CacheClientUpdaterStats-$serverLocation"), MessageStats {

    override fun getCommonTags(): Array<String> = arrayOf("serverLocation", serverLocation)

    private val clientUpdaterReceivedBytesMeter = CounterStatisticMeter("client.updater.received.bytes.count", "Total number of bytes received from the server.", unit = "bytes")
    private val clientUpdateMessagesReceivedMeter = GaugeStatisticMeter("client.updater.received.messages.count", "Current number of message being received off the network or being processed after reception.")
    private val clientUpdateMessagesReceivedBytesMeter = GaugeStatisticMeter("client.updater.received.messages.bytes", "Current number of bytes consumed by messages being received or processed.", unit = "bytes")

    override fun initializeStaticMeters() {
        registerMeter(clientUpdaterReceivedBytesMeter)
        registerMeter(clientUpdateMessagesReceivedMeter)
        registerMeter(clientUpdateMessagesReceivedBytesMeter)
    }

    override fun incReceivedBytes(bytes: Long) {
        clientUpdaterReceivedBytesMeter.increment(bytes)
    }

    override fun incSentBytes(v: Long) {
        // noop since we never send messages
    }

    override fun incMessagesBytesBeingReceived(bytes: Int) {
        clientUpdateMessagesReceivedBytesMeter.increment(bytes)
    }

    override fun decMessagesBytesBeingReceived(bytes: Int) {
        clientUpdateMessagesReceivedBytesMeter.decrement(bytes)
    }

    fun incMessagesBeingReceived(bytes: Int) {
        clientUpdateMessagesReceivedMeter.increment()
        incMessagesBytesBeingReceived(bytes)
    }

    fun decMessagesBeingReceived(bytes: Int) {
        clientUpdateMessagesReceivedMeter.decrement()
        decMessagesBytesBeingReceived(bytes)
    }
}
