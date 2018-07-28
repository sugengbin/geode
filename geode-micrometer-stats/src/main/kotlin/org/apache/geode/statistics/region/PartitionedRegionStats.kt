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

package org.apache.geode.statistics.region

import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup


class PartitionedRegionStats(name: String) : MicrometerMeterGroup("PartitionRegionStats-$name") {
    /**
     * Utility map for temporarily holding stat start times.
     *
     *
     * This was originally added to avoid having to add a long volunteeringStarted variable to every
     * instance of BucketAdvisor. Majority of BucketAdvisors never volunteer and an instance of
     * BucketAdvisor exists for every bucket defined in a PartitionedRegion which could result in a
     * lot of unused longs. Volunteering is a rare event and thus the performance implications of a
     * HashMap lookup is small and preferrable to so many longs. Key: BucketAdvisor, Value: Long
     */
    private val startTimeMap: MutableMap<*, *>


    var configuredRedundantCopies: Int
        set(`val`) {
            this.stats.setInt(configuredRedundantCopiesId, `val`)
        }

    var actualRedundantCopies: Int
        set(`val`) {
            this.stats.setInt(actualRedundantCopiesId, `val`)
        }

    private val temp = GaugeStatisticMeter("bucketCount", "Number of buckets in this node.")
    private val temp = CounterStatisticMeter("putsCompleted", "Number of puts completed.")
    private val temp = CounterStatisticMeter("putOpsRetried",
            "Number of put operations which had to be retried due to failures.")
    private val temp = CounterStatisticMeter("putRetries",
            "Total number of times put operations had to be retried.")
    private val temp = CounterStatisticMeter("createsCompleted", "Number of creates completed.")
    private val temp = CounterStatisticMeter("createOpsRetried",
            "Number of create operations which had to be retried due to failures.")
    private val temp = CounterStatisticMeter("createRetries",
            "Total number of times put operations had to be retried.")
    private val temp = CounterStatisticMeter("preferredReadLocal", "Number of reads satisfied from local store")
    private val temp = CounterStatisticMeter(PUTALLS_COMPLETED, "Number of putAlls completed.")
    private val temp = CounterStatisticMeter(PUTALL_MSGS_RETRIED,
            "Number of putAll messages which had to be retried due to failures.")
    private val temp = CounterStatisticMeter(PUTALL_RETRIES,
            "Total number of times putAll messages had to be retried.")
    private val temp = CounterStatisticMeter(PUTALL_TIME, "Total time spent doing putAlls.", unit="nanoseconds")
    private val temp = CounterStatisticMeter(REMOVE_ALLS_COMPLETED, "Number of removeAlls completed.")
    private val temp = CounterStatisticMeter(REMOVE_ALL_MSGS_RETRIED,
            "Number of removeAll messages which had to be retried due to failures.")
    private val temp = CounterStatisticMeter(REMOVE_ALL_RETRIES,
            "Total number of times removeAll messages had to be retried.")
    private val temp = CounterStatisticMeter(REMOVE_ALL_TIME, "Total time spent doing removeAlls.",
            unit="nanoseconds")
    private val temp = CounterStatisticMeter("preferredReadRemote", "Number of reads satisfied from remote store")
    private val temp = CounterStatisticMeter("getsCompleted", "Number of gets completed.")
    private val temp = CounterStatisticMeter("getOpsRetried",
            "Number of get operations which had to be retried due to failures.")
    private val temp = CounterStatisticMeter("getRetries",
            "Total number of times get operations had to be retried.")
    private val temp = CounterStatisticMeter("destroysCompleted", "Number of destroys completed.")
    private val temp = CounterStatisticMeter("destroyOpsRetried",
            "Number of destroy operations which had to be retried due to failures.")
    private val temp = CounterStatisticMeter("destroyRetries",
            "Total number of times destroy operations had to be retried.")
    private val temp = CounterStatisticMeter("invalidatesCompleted", "Number of invalidates completed.")

    private val temp = CounterStatisticMeter("invalidateOpsRetried",
            "Number of invalidate operations which had to be retried due to failures.")
    private val temp = CounterStatisticMeter("invalidateRetries",
            "Total number of times invalidate operations had to be retried.")
    private val temp = CounterStatisticMeter("containsKeyCompleted", "Number of containsKeys completed.")

    private val temp = CounterStatisticMeter("containsKeyOpsRetried",
            "Number of containsKey or containsValueForKey operations which had to be retried due to failures.")
    private val temp = CounterStatisticMeter("containsKeyRetries",
            "Total number of times containsKey or containsValueForKey operations had to be retried.")
    private val temp = CounterStatisticMeter("containsValueForKeyCompleted",
            "Number of containsValueForKeys completed.")
    private val temp = CounterStatisticMeter("PartitionMessagesSent", "Number of PartitionMessages Sent.")
    private val temp = CounterStatisticMeter("PartitionMessagesReceived", "Number of PartitionMessages Received.")
    private val temp = CounterStatisticMeter("PartitionMessagesProcessed",
            "Number of PartitionMessages Processed.")
    private val temp = CounterStatisticMeter("putTime", "Total time spent doing puts.", unit="nanoseconds")
    private val temp = CounterStatisticMeter("createTime", "Total time spent doing create operations.",
            unit="nanoseconds", false)
    private val temp = CounterStatisticMeter("getTime", "Total time spent performing get operations.",
            unit="nanoseconds", false)
    private val temp = CounterStatisticMeter("destroyTime", "Total time spent doing destroys.", unit="nanoseconds",)
    private val temp = CounterStatisticMeter("invalidateTime", "Total time spent doing invalidates.",
            unit="nanoseconds")
    private val temp = CounterStatisticMeter("containsKeyTime",
            "Total time spent performing containsKey operations.", unit="nanoseconds")
    private val temp = CounterStatisticMeter("containsValueForKeyTime",
            "Total time spent performing containsValueForKey operations.", unit="nanoseconds")
    private val temp = CounterStatisticMeter("partitionMessagesProcessingTime",
            "Total time spent on PartitionMessages processing.", unit="nanoseconds"
    private val temp = GaugeStatisticMeter("dataStoreEntryCount",
            "The number of entries stored in this Cache for the named Partitioned Region. This does not include entries which are tombstones. See CachePerfStats.tombstoneCount.")
    private val temp = GaugeStatisticMeter("dataStoreBytesInUse",
            "The current number of bytes stored in this Cache for the named Partitioned Region")
    private val temp = GaugeStatisticMeter("volunteeringInProgress",
            "Current number of attempts to volunteer for primary of a bucket.")
    private val temp = CounterStatisticMeter("volunteeringBecamePrimary",
            "Total number of attempts to volunteer that ended when this member became primary.")
    private val temp = CounterStatisticMeter("volunteeringBecamePrimaryTime",
            "Total time spent volunteering that ended when this member became primary.",
            unit="nanoseconds")
    private val temp = CounterStatisticMeter("volunteeringOtherPrimary",
            "Total number of attempts to volunteer that ended when this member discovered other primary.")
    private val temp = CounterStatisticMeter("volunteeringOtherPrimaryTime",
            "Total time spent volunteering that ended when this member discovered other primary.",
            unit="nanoseconds")
    private val temp = CounterStatisticMeter("volunteeringClosed",
            "Total number of attempts to volunteer that ended when this member's bucket closed.")
    private val temp = CounterStatisticMeter("volunteeringClosedTime",
            "Total time spent volunteering that ended when this member's bucket closed.",
            unit="nanoseconds")
    private val temp = GaugeStatisticMeter("totalNumBuckets", "The total number of buckets.")
    private val temp = GaugeStatisticMeter("primaryBucketCount",
            "Current number of primary buckets hosted locally.")
    private val temp = GaugeStatisticMeter("volunteeringThreads",
            "Current number of threads volunteering for primary.")
    private val temp = GaugeStatisticMeter("lowRedundancyBucketCount",
            "Current number of buckets without full redundancy.")
    private val temp = GaugeStatisticMeter("noCopiesBucketCount",
            "Current number of buckets without any copies remaining.")
    private val temp = GaugeStatisticMeter("configuredRedundantCopies",
            "Configured number of redundant copies for this partitioned region.")
    private val temp = GaugeStatisticMeter("actualRedundantCopies",
            "Actual number of redundant copies for this partitioned region.")
    private val temp = CounterStatisticMeter("getEntryCompleted", "Number of getEntry operations completed.")
    private val temp = CounterStatisticMeter("getEntryTime", "Total time spent performing getEntry operations.",
            unit="nanoseconds")

    private val temp = GaugeStatisticMeter("recoveriesInProgress",
            "Current number of redundancy recovery operations in progress for this region.")
    private val temp = CounterStatisticMeter("recoveriesCompleted",
            "Total number of redundancy recovery operations performed on this region.")
    private val temp = CounterStatisticMeter("recoveryTime", "Total number time spent recovering redundancy.")
    private val temp = GaugeStatisticMeter("bucketCreatesInProgress",
            "Current number of bucket create operations being performed for rebalancing.")
    private val temp = CounterStatisticMeter("bucketCreatesCompleted",
            "Total number of bucket create operations performed for rebalancing.")
    private val temp = CounterStatisticMeter("bucketCreatesFailed",
            "Total number of bucket create operations performed for rebalancing that failed.")
    private val temp = CounterStatisticMeter("bucketCreateTime",
            "Total time spent performing bucket create operations for rebalancing.",
            unit="nanoseconds")
    private val temp = GaugeStatisticMeter("primaryTransfersInProgress",
            "Current number of primary transfer operations being performed for rebalancing.")
    private val temp = CounterStatisticMeter("primaryTransfersCompleted",
            "Total number of primary transfer operations performed for rebalancing.")
    private val temp = CounterStatisticMeter("primaryTransfersFailed",
            "Total number of primary transfer operations performed for rebalancing that failed.")
    private val temp = CounterStatisticMeter("primaryTransferTime",
            "Total time spent performing primary transfer operations for rebalancing.",
            unit="nanoseconds")

    private val temp = CounterStatisticMeter("applyReplicationCompleted",
            "Total number of replicated values sent from a primary to this redundant data store.")
    private val temp = GaugeStatisticMeter("applyReplicationInProgress",
            "Current number of replication operations in progress on this redundant data store.")
    private val temp = CounterStatisticMeter("applyReplicationTime",
            "Total time spent storing replicated values on this redundant data store.",
            unit="nanoseconds")
    private val temp = CounterStatisticMeter("sendReplicationCompleted",
            "Total number of replicated values sent from this primary to a redundant data store.")
    private val temp = GaugeStatisticMeter("sendReplicationInProgress",
            "Current number of replication operations in progress from this primary.")
    private val temp = CounterStatisticMeter("sendReplicationTime",
            "Total time spent replicating values from this primary to a redundant data store.",
            unit="nanoseconds")
    private val temp = CounterStatisticMeter("putRemoteCompleted",
            "Total number of completed puts that did not originate in the primary. These puts require an extra network hop to the primary.")
    private val temp = GaugeStatisticMeter("putRemoteInProgress",
            "Current number of puts in progress that did not originate in the primary.")
    private val temp = CounterStatisticMeter("putRemoteTime",
            "Total time spent doing puts that did not originate in the primary.", unit="nanoseconds")
    private val temp = CounterStatisticMeter("putLocalCompleted",
            "Total number of completed puts that did originate in the primary. These puts are optimal.")
    private val temp = GaugeStatisticMeter("putLocalInProgress",
            "Current number of puts in progress that did originate in the primary.")
    private val temp = CounterStatisticMeter("putLocalTime",
            "Total time spent doing puts that did originate in the primary.", unit="nanoseconds")

    private val temp = GaugeStatisticMeter("rebalanceBucketCreatesInProgress",
            "Current number of bucket create operations being performed for rebalancing.")
    private val temp = CounterStatisticMeter("rebalanceBucketCreatesCompleted",
            "Total number of bucket create operations performed for rebalancing.")
    private val temp = CounterStatisticMeter("rebalanceBucketCreatesFailed",
            "Total number of bucket create operations performed for rebalancing that failed.")
    private val temp = CounterStatisticMeter("rebalanceBucketCreateTime",
            "Total time spent performing bucket create operations for rebalancing.", unit="nanoseconds")
    private val temp = GaugeStatisticMeter("rebalancePrimaryTransfersInProgress",
            "Current number of primary transfer operations being performed for rebalancing.")
    private val temp = CounterStatisticMeter("rebalancePrimaryTransfersCompleted",
            "Total number of primary transfer operations performed for rebalancing.")
    private val temp = CounterStatisticMeter("rebalancePrimaryTransfersFailed",
            "Total number of primary transfer operations performed for rebalancing that failed.")
    private val temp = CounterStatisticMeter("rebalancePrimaryTransferTime",
            "Total time spent performing primary transfer operations for rebalancing.", unit="nanoseconds")
    private val temp = CounterStatisticMeter("prMetaDataSentCount", "total number of times meta data refreshed sent on client's request.")

    private val temp = GaugeStatisticMeter("localMaxMemory", "local max memory in bytes for this region on this member")

    @JvmOverloads
    fun endPut(start: Long, numInc: Int = 1) {
        if (CachePerfStats.enableClockStats) {
            val delta = CachePerfStats.getStatTime() - start
            this.stats.incLong(putTimeId, delta)
        }
        this.stats.incInt(putsCompletedId, numInc)
    }

    /**
     * This method sets the end time for putAll and updates the counters
     *
     */
    @JvmOverloads
    fun endPutAll(start: Long, numInc: Int = 1) {
        if (CachePerfStats.enableClockStats) {
            val delta = CachePerfStats.getStatTime() - start
            this.stats.incLong(fieldId_PUTALL_TIME, delta)
            // this.putStatsHistogram.endOp(delta);

        }
        this.stats.incInt(fieldId_PUTALLS_COMPLETED, numInc)
    }

    @JvmOverloads
    fun endRemoveAll(start: Long, numInc: Int = 1) {
        if (CachePerfStats.enableClockStats) {
            val delta = CachePerfStats.getStatTime() - start
            this.stats.incLong(fieldId_REMOVE_ALL_TIME, delta)
        }
        this.stats.incInt(fieldId_REMOVE_ALLS_COMPLETED, numInc)
    }

    @JvmOverloads
    fun endCreate(start: Long, numInc: Int = 1) {
        if (CachePerfStats.enableClockStats) {
            this.stats.incLong(createTimeId, CachePerfStats.getStatTime() - start)
        }
        this.stats.incInt(createsCompletedId, numInc)
    }

    @JvmOverloads
    fun endGet(start: Long, numInc: Int = 1) {
        if (CachePerfStats.enableClockStats) {
            val delta = CachePerfStats.getStatTime() - start
            this.stats.incLong(getTimeId, delta)
        }
        this.stats.incInt(getsCompletedId, numInc)
    }

    fun endDestroy(start: Long) {
        if (CachePerfStats.enableClockStats) {
            this.stats.incLong(destroyTimeId, CachePerfStats.getStatTime() - start)
        }
        this.stats.incInt(destroysCompletedId, 1)
    }

    fun endInvalidate(start: Long) {
        if (CachePerfStats.enableClockStats) {
            this.stats.incLong(invalidateTimeId, CachePerfStats.getStatTime() - start)
        }
        this.stats.incInt(invalidatesCompletedId, 1)
    }

    @JvmOverloads
    fun endContainsKey(start: Long, numInc: Int = 1) {
        if (CachePerfStats.enableClockStats) {
            this.stats.incLong(containsKeyTimeId, CachePerfStats.getStatTime() - start)
        }
        this.stats.incInt(containsKeyCompletedId, numInc)
    }

    @JvmOverloads
    fun endContainsValueForKey(start: Long, numInc: Int = 1) {
        if (CachePerfStats.enableClockStats) {
            this.stats.incLong(containsValueForKeyTimeId, CachePerfStats.getStatTime() - start)
        }
        this.stats.incInt(containsValueForKeyCompletedId, numInc)
    }

    fun incContainsKeyValueRetries() {
        this.stats.incInt(containsKeyRetriesId, 1)
    }

    fun incContainsKeyValueOpsRetried() {
        this.stats.incInt(containsKeyOpsRetriedId, 1)
    }

    fun incInvalidateRetries() {
        this.stats.incInt(invalidateRetriesId, 1)
    }

    fun incInvalidateOpsRetried() {
        this.stats.incInt(invalidateOpsRetriedId, 1)
    }

    fun incDestroyRetries() {
        this.stats.incInt(destroyRetriesId, 1)
    }

    fun incDestroyOpsRetried() {
        this.stats.incInt(destroyOpsRetriedId, 1)
    }

    fun incPutRetries() {
        this.stats.incInt(putRetriesId, 1)
    }

    fun incPutOpsRetried() {
        this.stats.incInt(putOpsRetriedId, 1)
    }

    fun incGetOpsRetried() {
        this.stats.incInt(getOpsRetriedId, 1)
    }

    fun incGetRetries() {
        this.stats.incInt(getRetriesId, 1)
    }

    fun incCreateOpsRetried() {
        this.stats.incInt(createOpsRetriedId, 1)
    }

    fun incCreateRetries() {
        this.stats.incInt(createRetriesId, 1)
    }

    // ------------------------------------------------------------------------
    // preferred read stats
    // ------------------------------------------------------------------------

    fun incPreferredReadLocal() {
        this.stats.incInt(preferredReadLocalId, 1)
    }

    fun incPreferredReadRemote() {
        this.stats.incInt(preferredReadRemoteId, 1)
    }

    // ------------------------------------------------------------------------
    // messaging stats
    // ------------------------------------------------------------------------

    fun startPartitionMessageProcessing(): Long {
        this.stats.incInt(partitionMessagesReceivedId, 1)
        return startTime()
    }

    fun endPartitionMessagesProcessing(start: Long) {
        if (CachePerfStats.enableClockStats) {
            val delta = CachePerfStats.getStatTime() - start
            this.stats.incLong(partitionMessagesProcessingTimeId, delta)
        }
        this.stats.incInt(partitionMessagesProcessedId, 1)
    }

    fun incPartitionMessagesSent() {
        this.stats.incInt(partitionMessagesSentId, 1)
    }

    // ------------------------------------------------------------------------
    // datastore stats
    // ------------------------------------------------------------------------

    fun incBucketCount(delta: Int) {
        this.stats.incInt(bucketCountId, delta)
    }

    fun setBucketCount(i: Int) {
        this.stats.setInt(bucketCountId, i)
    }

    fun incDataStoreEntryCount(amt: Int) {
        this.stats.incInt(dataStoreEntryCountId, amt)
    }

    fun incBytesInUse(delta: Long) {
        this.stats.incLong(dataStoreBytesInUseId, delta)
    }

    fun incPutAllRetries() {
        this.stats.incInt(fieldId_PUTALL_RETRIES, 1)
    }

    fun incPutAllMsgsRetried() {
        this.stats.incInt(fieldId_PUTALL_MSGS_RETRIED, 1)
    }

    fun incRemoveAllRetries() {
        this.stats.incInt(fieldId_REMOVE_ALL_RETRIES, 1)
    }

    fun incRemoveAllMsgsRetried() {
        this.stats.incInt(fieldId_REMOVE_ALL_MSGS_RETRIED, 1)
    }

    fun startVolunteering(): Long {
        this.stats.incInt(volunteeringInProgressId, 1)
        return CachePerfStats.getStatTime()
    }

    fun endVolunteeringBecamePrimary(start: Long) {
        val ts = CachePerfStats.getStatTime()
        this.stats.incInt(volunteeringInProgressId, -1)
        this.stats.incInt(volunteeringBecamePrimaryId, 1)
        if (CachePerfStats.enableClockStats) {
            val time = ts - start
            this.stats.incLong(volunteeringBecamePrimaryTimeId, time)
        }
    }

    fun endVolunteeringOtherPrimary(start: Long) {
        val ts = CachePerfStats.getStatTime()
        this.stats.incInt(volunteeringInProgressId, -1)
        this.stats.incInt(volunteeringOtherPrimaryId, 1)
        if (CachePerfStats.enableClockStats) {
            val time = ts - start
            this.stats.incLong(volunteeringOtherPrimaryTimeId, time)
        }
    }

    fun endVolunteeringClosed(start: Long) {
        val ts = CachePerfStats.getStatTime()
        this.stats.incInt(volunteeringInProgressId, -1)
        this.stats.incInt(volunteeringClosedId, 1)
        if (CachePerfStats.enableClockStats) {
            val time = ts - start
            this.stats.incLong(volunteeringClosedTimeId, time)
        }
    }

    fun incTotalNumBuckets(`val`: Int) {
        this.stats.incInt(totalNumBucketsId, `val`)
    }

    fun incPrimaryBucketCount(`val`: Int) {
        this.stats.incInt(primaryBucketCountId, `val`)
    }

    fun incVolunteeringThreads(`val`: Int) {
        this.stats.incInt(volunteeringThreadsId, `val`)
    }

    fun incLowRedundancyBucketCount(`val`: Int) {
        this.stats.incInt(lowRedundancyBucketCountId, `val`)
    }

    fun incNoCopiesBucketCount(`val`: Int) {
        this.stats.incInt(noCopiesBucketCountId, `val`)
    }

    fun setLocalMaxMemory(l: Long) {
        this.stats.setLong(localMaxMemoryId, l)
    }

    // ------------------------------------------------------------------------
    // startTimeMap methods
    // ------------------------------------------------------------------------

    /** Put stat start time in holding map for later removal and use by caller  */
    fun putStartTime(key: Any, startTime: Long) {
        if (CachePerfStats.enableClockStats) {
            this.startTimeMap[key] = java.lang.Long.valueOf(startTime)
        }
    }

    /** Remove stat start time from holding map to complete a clock stat  */
    fun removeStartTime(key: Any): Long {
        val startTime = this.startTimeMap.remove(key) as Long
        return startTime ?: 0
    }

    /**
     * Statistic to track the [Region.getEntry] call
     *
     * @param startTime the time the getEntry operation started
     */
    fun endGetEntry(startTime: Long) {
        endGetEntry(startTime, 1)
    }

    /**
     * This method sets the end time for update and updates the counters
     *
     */
    fun endGetEntry(start: Long, numInc: Int) {
        if (CachePerfStats.enableClockStats) {
            this.stats.incLong(getEntryTimeId, CachePerfStats.getStatTime() - start)
        }
        this.stats.incInt(getEntriesCompletedId, numInc)
    }

    // ------------------------------------------------------------------------
    // bucket creation, primary transfer stats (see also rebalancing stats below)
    // ------------------------------------------------------------------------
    fun startRecovery(): Long {
        this.stats.incInt(recoveriesInProgressId, 1)
        return PartitionedRegionStats.statTime
    }

    fun endRecovery(start: Long) {
        val ts = PartitionedRegionStats.statTime
        this.stats.incInt(recoveriesInProgressId, -1)
        if (CachePerfStats.enableClockStats) {
            this.stats.incLong(recoveriesTimeId, ts - start)
        }
        this.stats.incInt(recoveriesCompletedId, 1)
    }

    fun startBucketCreate(isRebalance: Boolean): Long {
        this.stats.incInt(bucketCreatesInProgressId, 1)
        if (isRebalance) {
            startRebalanceBucketCreate()
        }
        return PartitionedRegionStats.statTime
    }

    fun endBucketCreate(start: Long, success: Boolean, isRebalance: Boolean) {
        val ts = PartitionedRegionStats.statTime
        this.stats.incInt(bucketCreatesInProgressId, -1)
        if (CachePerfStats.enableClockStats) {
            this.stats.incLong(bucketCreateTimeId, ts - start)
        }
        if (success) {
            this.stats.incInt(bucketCreatesCompletedId, 1)
        } else {
            this.stats.incInt(bucketCreatesFailedId, 1)
        }
        if (isRebalance) {
            endRebalanceBucketCreate(start, ts, success)
        }
    }

    fun startPrimaryTransfer(isRebalance: Boolean): Long {
        this.stats.incInt(primaryTransfersInProgressId, 1)
        if (isRebalance) {
            startRebalancePrimaryTransfer()
        }
        return PartitionedRegionStats.statTime
    }

    fun endPrimaryTransfer(start: Long, success: Boolean, isRebalance: Boolean) {
        val ts = PartitionedRegionStats.statTime
        this.stats.incInt(primaryTransfersInProgressId, -1)
        if (CachePerfStats.enableClockStats) {
            this.stats.incLong(primaryTransferTimeId, ts - start)
        }
        if (success) {
            this.stats.incInt(primaryTransfersCompletedId, 1)
        } else {
            this.stats.incInt(primaryTransfersFailedId, 1)
        }
        if (isRebalance) {
            endRebalancePrimaryTransfer(start, ts, success)
        }
    }

    // ------------------------------------------------------------------------
    // rebalancing stats
    // ------------------------------------------------------------------------

    private fun startRebalanceBucketCreate() {
        this.stats.incInt(rebalanceBucketCreatesInProgressId, 1)
    }

    private fun endRebalanceBucketCreate(start: Long, end: Long, success: Boolean) {
        this.stats.incInt(rebalanceBucketCreatesInProgressId, -1)
        if (CachePerfStats.enableClockStats) {
            this.stats.incLong(rebalanceBucketCreateTimeId, end - start)
        }
        if (success) {
            this.stats.incInt(rebalanceBucketCreatesCompletedId, 1)
        } else {
            this.stats.incInt(rebalanceBucketCreatesFailedId, 1)
        }
    }

    private fun startRebalancePrimaryTransfer() {
        this.stats.incInt(rebalancePrimaryTransfersInProgressId, 1)
    }

    private fun endRebalancePrimaryTransfer(start: Long, end: Long, success: Boolean) {
        this.stats.incInt(rebalancePrimaryTransfersInProgressId, -1)
        if (CachePerfStats.enableClockStats) {
            this.stats.incLong(rebalancePrimaryTransferTimeId, end - start)
        }
        if (success) {
            this.stats.incInt(rebalancePrimaryTransfersCompletedId, 1)
        } else {
            this.stats.incInt(rebalancePrimaryTransfersFailedId, 1)
        }
    }

    fun startApplyReplication(): Long {
        stats.incInt(applyReplicationInProgressId, 1)
        return CachePerfStats.getStatTime()
    }

    fun endApplyReplication(start: Long) {
        val delta = CachePerfStats.getStatTime() - start
        stats.incInt(applyReplicationInProgressId, -1)
        stats.incInt(applyReplicationCompletedId, 1)
        stats.incLong(applyReplicationTimeId, delta)
    }

    fun startSendReplication(): Long {
        stats.incInt(sendReplicationInProgressId, 1)
        return CachePerfStats.getStatTime()
    }

    fun endSendReplication(start: Long) {
        val delta = CachePerfStats.getStatTime() - start
        stats.incInt(sendReplicationInProgressId, -1)
        stats.incInt(sendReplicationCompletedId, 1)
        stats.incLong(sendReplicationTimeId, delta)
    }

    fun startPutRemote(): Long {
        stats.incInt(putRemoteInProgressId, 1)
        return CachePerfStats.getStatTime()
    }

    fun endPutRemote(start: Long) {
        val delta = CachePerfStats.getStatTime() - start
        stats.incInt(putRemoteInProgressId, -1)
        stats.incInt(putRemoteCompletedId, 1)
        stats.incLong(putRemoteTimeId, delta)
    }

    fun startPutLocal(): Long {
        stats.incInt(putLocalInProgressId, 1)
        return CachePerfStats.getStatTime()
    }

    fun endPutLocal(start: Long) {
        val delta = CachePerfStats.getStatTime() - start
        stats.incInt(putLocalInProgressId, -1)
        stats.incInt(putLocalCompletedId, 1)
        stats.incLong(putLocalTimeId, delta)
    }

    fun incPRMetaDataSentCount() {
        this.stats.incLong(prMetaDataSentCountId, 1)
    }

    companion object {

        private val PUTALLS_COMPLETED = "putAllsCompleted"
        private val PUTALL_MSGS_RETRIED = "putAllMsgsRetried"
        private val PUTALL_RETRIES = "putAllRetries"
        private val PUTALL_TIME = "putAllTime"

        private val REMOVE_ALLS_COMPLETED = "removeAllsCompleted"
        private val REMOVE_ALL_MSGS_RETRIED = "removeAllMsgsRetried"
        private val REMOVE_ALL_RETRIES = "removeAllRetries"
        private val REMOVE_ALL_TIME = "removeAllTime"

        fun startTime(): Long {
            return CachePerfStats.getStatTime()
        }

        val statTime: Long
            get() = CachePerfStats.getStatTime()
    }
}// ------------------------------------------------------------------------
// region op stats
// ------------------------------------------------------------------------
/**
 * This method sets the end time for putAll and updates the counters
 *
 */
