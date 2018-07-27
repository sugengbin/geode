package org.apache.geode.internal.cache.tier.sockets;

import org.apache.geode.distributed.internal.DistributionStats;
import org.apache.geode.distributed.internal.ServerLocation;

/**
 * Stats for a CacheClientUpdater. Currently the only thing measured are incoming bytes on the
 * wire
 *
 * @since GemFire 5.7
 */
public class CCUStats implements MessageStats {

  private StatisticsType type;
  private int messagesBeingReceivedId;
  private int messageBytesBeingReceivedId;
  private int receivedBytesId;

  private void initializeStats(StatisticsFactory factory) {
    type = factory.createType("CacheClientUpdaterStats", "Statistics about incoming subscription data",
        new StatisticDescriptor[] {
            factory.createLongCounter("receivedBytes",
                "Total number of bytes received from the server.", "bytes"),
            factory.createIntGauge("messagesBeingReceived",
                "Current number of message being received off the network or being processed after reception.",
                "messages"),
            factory.createLongGauge("messageBytesBeingReceived",
                "Current number of bytes consumed by messages being received or processed.",
                "bytes"),});
    receivedBytesId = type.nameToId("receivedBytes");
    messagesBeingReceivedId = type.nameToId("messagesBeingReceived");
    messageBytesBeingReceivedId = type.nameToId("messageBytesBeingReceived");
  }

  // instance fields
  private final Statistics stats;

  CCUStats(StatisticsFactory factory, ServerLocation location) {
    initializeStats(factory);
    // no need for atomic since only a single thread will be writing these
    this.stats = factory.createStatistics(type, "CacheClientUpdater-" + location);
  }

  public void close() {
    this.stats.close();
  }

  @Override
  public void incReceivedBytes(long v) {
    this.stats.incLong(receivedBytesId, v);
  }

  @Override
  public void incSentBytes(long v) {
    // noop since we never send messages
  }

  @Override
  public void incMessagesBeingReceived(int bytes) {
    this.stats.incInt(messagesBeingReceivedId, 1);
    if (bytes > 0) {
      this.stats.incLong(messageBytesBeingReceivedId, bytes);
    }
  }

  @Override
  public void decMessagesBeingReceived(int bytes) {
    this.stats.incInt(messagesBeingReceivedId, -1);
    if (bytes > 0) {
      this.stats.incLong(messageBytesBeingReceivedId, -bytes);
    }
  }

  /**
   * Returns the current time (ns).
   *
   * @return the current time (ns)
   */
  public long startTime() {
    return DistributionStats.getStatTime();
  }
}
