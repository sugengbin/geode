package org.apache.geode.statistics.cache

import org.apache.geode.distributed.internal.PoolStatHelper
import org.apache.geode.distributed.internal.QueueStatHelper
import org.apache.geode.statistics.internal.micrometer.CounterStatisticMeter
import org.apache.geode.statistics.internal.micrometer.GaugeStatisticMeter
import org.apache.geode.statistics.internal.micrometer.MicrometerMeterGroup
import org.apache.geode.statistics.internal.micrometer.TimerStatisticMeter
import org.apache.geode.statistics.util.NOW_NANOS

open class CachePerfStats(regionName: String?) : MicrometerMeterGroup("CachePerfStats${regionName?.let { "-$it" }
        ?: ""}") {

    constructor() : this(null)

    private val cachePerfStatsPrefix: String by lazy {
        regionName?.let { "region." } ?: "cachePerf."
    }
    private val meterTagDefaultArray: Array<String> by lazy {
        regionName?.let { arrayOf("regionName", regionName) } ?: emptyArray()
    }


    val regionLoadsInProgressMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.loads.inprogress", "Current number of threads in this cache doing a cache load.", meterTagDefaultArray)
    val regionLoadsCompletedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.loads.completed", "Total number of times a load on this cache has completed (as a result of either a local get() or a remote netload).", meterTagDefaultArray)
    val regionLoadsTimer = TimerStatisticMeter("$cachePerfStatsPrefix.load.time", "Total time spent invoking loaders on this cache.", meterTagDefaultArray, unit = "nanoseconds")
    val regionNetLoadInProgressMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.netload.inprogress", "Current number of threads doing a network load initiated by a get() in this cache.", meterTagDefaultArray)
    val regionNetLoadCompletedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.netload.completed", "Total number of times a network load initiated on this cache has completed.", meterTagDefaultArray)
    val regionNetLoadTimer = TimerStatisticMeter("$cachePerfStatsPrefix.netloadTime", "Total time spent doing network loads on this cache.", meterTagDefaultArray, unit = "nanoseconds")
    val regionNetSearchInProgressMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.netsearch.inprogress", "Current number of threads doing a network search initiated by a get() in this cache.", meterTagDefaultArray)
    val regionNetSearchCompletedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.netsearch.completed", "Total number of times network searches initiated by this cache have completed.", meterTagDefaultArray)
    val regionNetSearchTimer = TimerStatisticMeter("$cachePerfStatsPrefix.netsearch.time", "Total time spent doing network searches for cache values.", meterTagDefaultArray, unit = "nanoseconds")
    val regionCacheWriterInProgressMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.cachewriter.inprogress", "Current number of threads doing a cache writer call.", meterTagDefaultArray)
    val regionCacheWriterCompletedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.cachewriter.completed", "Total number of times a cache writer call has completed.", meterTagDefaultArray)
    val regionCacheWriteTimer = TimerStatisticMeter("$cachePerfStatsPrefix.cachewriter.time", "Total time spent doing cache writer calls.", meterTagDefaultArray, unit = "nanoseconds")

    val regionCacheListenerInProgressMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.cachelistener.inprogress", "Current number of threads doing a cache listener call.", meterTagDefaultArray)
    val regionCacheListenerCompletedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.cachelistener.completed", "Total number of times a cache listener call has completed.", meterTagDefaultArray)
    val regionCacheListenerTimer = TimerStatisticMeter("$cachePerfStatsPrefix.cachelistener.time", "Total time spent doing cache listener calls.", meterTagDefaultArray, unit = "nanoseconds")
    val regionIndexUpdateInProgressMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.index.operations.inprogress", "Current number of ops in progress", meterTagDefaultArray.plus(arrayOf("operation", "update")))
    val regionIndexUpdateCompletedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.index.operations.completed", "Total number of ops that haves completed", meterTagDefaultArray.plus(arrayOf("operation", "update")))
    val regionIndexUpdateTimeMeter = TimerStatisticMeter("$cachePerfStatsPrefix.index.operations.time", "Total amount of time spent doing this op", meterTagDefaultArray.plus(arrayOf("operation", "update")), unit = "nanoseconds")
    val regionIndexInitializationInProgressMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.index.operations.inprogress", "Current number of index initializations in progress", meterTagDefaultArray.plus(arrayOf("operation", "initialization")))
    val regionIndexInitializationCompletedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.index.operations.completed", "Total number of index initializations that have completed", meterTagDefaultArray.plus(arrayOf("operation", "initialization")))
    val regionIndexInitializationTimeMeter = TimerStatisticMeter("$cachePerfStatsPrefix.index.operations.time", "Total amount of time spent initializing indexes", meterTagDefaultArray.plus(arrayOf("operation", "initialization")), unit = "nanoseconds")
    val regionGIIInProgressMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.getinitialimages.inprogress", "Current number of getInitialImage operations currently in progress.", meterTagDefaultArray)
    val regionGIICompletedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.getinitialimages.completed", "Total number of times getInitialImages (both delta and full GII) initiated by this cache have completed.", meterTagDefaultArray)
    val regionDeltaGIICompletedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.getinitialimages.delta.completed", "Total number of times delta getInitialImages initiated by this cache have completed.", meterTagDefaultArray)
    val regionGIITimer = TimerStatisticMeter("$cachePerfStatsPrefix.getinitialimages.time", "Total time spent doing getInitialImages for region creation.", meterTagDefaultArray, unit = "nanoseconds")
    val regionGIIKeysReceivedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.getinitialimages.keys.received", "Total number of keys received while doing getInitialImage operations.", meterTagDefaultArray)
    val numberOfRegionsMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.regions.count", "The current number of regions in the cache.", meterTagDefaultArray)
    val numberOfPartitionedRegionsMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.regions.count", "The current number of partitioned regions in the cache.", meterTagDefaultArray.plus(arrayOf("regionType", "partitioned")))
    val regionDestroyOperationMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.count", "The total number of times a cache object entry has been destroyed in this cache.", meterTagDefaultArray.plus(arrayOf("operationType", "destroy")))
    val regionUpdateOperationMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.count", "The total number of updates originating remotely that have been applied to this cache.", meterTagDefaultArray.plus(arrayOf("operationType", "update")))
    val regionUpdateOperationTimer = TimerStatisticMeter("$cachePerfStatsPrefix.operations.time", "Total time spent performing an update.", meterTagDefaultArray.plus(arrayOf("operationType", "update")), unit = "nanoseconds")
    val regionInvalidateOperationMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.count", "The total number of times an existing cache object entry value in this cache has been invalidated", meterTagDefaultArray.plus(arrayOf("operationType", "invalidate")))
    val regionGetOperationMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.count", "The total number of times a successful get has been done on this cache.", meterTagDefaultArray.plus(arrayOf("operationType", "get")))
    val regionGetOperationTimer = TimerStatisticMeter("$cachePerfStatsPrefix.operations.time", "Total time spent doing get operations from this cache (including netsearch and netload)", meterTagDefaultArray.plus(arrayOf("operationType", "get")), unit = "nanoseconds")
    val regionMissesMeter = CounterStatisticMeter("$cachePerfStatsPrefix.misses.count", "Total number of times a get on the cache did not find a value already in local memory. The number of hits (i.e. gets that did not miss) can be calculated by subtracting misses from gets.", meterTagDefaultArray)
    val regionCreateOperationMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.count", "The total number of times an entry is added to this cache.", meterTagDefaultArray.plus(arrayOf("operationType", "create")))
    val regionPutOperationMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.count", "The total number of times an entry is added or replaced in this cache as a result of a local operation (put() create() or get() which results in load, netsearch, or netloading a value). Note that this only counts puts done explicitly on this cache. It does not count updates pushed from other caches.", meterTagDefaultArray.plus(arrayOf("operationType", "put")))
    val regionPutOperationTimer = TimerStatisticMeter("$cachePerfStatsPrefix.operations.time", "Total time spent adding or replacing an entry in this cache as a result of a local operation.  This includes synchronizing on the map, invoking cache callbacks, sending messages to other caches and waiting for responses (if required).", meterTagDefaultArray.plus(arrayOf("operationType", "put")), unit = "nanoseconds")
    val regionPutAllOperationMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.count", "The total number of times a map is added or replaced in this cache as a result of a local operation. Note that this only counts putAlls done explicitly on this cache. It does not count updates pushed from other caches.", meterTagDefaultArray.plus(arrayOf("operationType", "putAll")))
    val regionPutAllOperationTimer = TimerStatisticMeter("$cachePerfStatsPrefix.operations.time", "Total time spent replacing a map in this cache as a result of a local operation.  This includes synchronizing on the map, invoking cache callbacks, sending messages to other caches and waiting for responses (if required).", meterTagDefaultArray.plus(arrayOf("operationType", "putAll")), unit = "nanoseconds")
    val regionRemoveAllOperationMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.count", "The total number of removeAll operations that originated in this cache. Note that this only counts removeAlls done explicitly on this cache. It does not count removes pushed from other caches.", meterTagDefaultArray.plus(arrayOf("operationType", "removeAll")))
    val regionRemoveAllOperationTimer = TimerStatisticMeter("$cachePerfStatsPrefix.operations.count", "Total time spent performing removeAlls that originated in this cache. This includes time spent waiting for the removeAll to be done in remote caches (if required).", meterTagDefaultArray.plus(arrayOf("operationType", "destroy")), unit = "nanoseconds")

    val regionEventQueueSizeMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.eventqueue.size", "The number of cache events waiting to be processed.", meterTagDefaultArray)
    val regionEventQueueThrottleMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.eventqueue.throttle.count", "The total number of times a thread was delayed in adding an event to the event queue.", meterTagDefaultArray)
    val regionEventQueueThrottleTimer = TimerStatisticMeter("$cachePerfStatsPrefix.eventqueue.throttle.time", "The total amount of time, in nanoseconds, spent delayed by the event queue throttle.", meterTagDefaultArray, unit = "nanoseconds")
    val regionEventQueueThreadMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.eventqueue.thread.count", "The number of threads currently processing events.", meterTagDefaultArray)

    val regionQueryExecutionMeter = CounterStatisticMeter("$cachePerfStatsPrefix.query.execution.count", "Total number of times some query has been executed", meterTagDefaultArray)
    val regionQueryExecutionTimer = TimerStatisticMeter("$cachePerfStatsPrefix.query.execution.time", "Total time spent executing queries", meterTagDefaultArray, unit = "nanoseconds")
    val regionQueryResultsHashCollisionsMeter = CounterStatisticMeter("$cachePerfStatsPrefix.query.results.hashcollisions.count", "Total number of times an hash code collision occurred when inserting an object into an OQL result set or rehashing it", meterTagDefaultArray)
    val regionQueryResultsHashCollisionsTimer = TimerStatisticMeter("$cachePerfStatsPrefix.query.results.hashcollisions.time", "Total time spent probing the hashtable in an OQL result set due to hash code collisions, includes reads, writes, and rehashes", meterTagDefaultArray, unit = "nanoseconds")
    val regionQueryExecutionRetryMeter = CounterStatisticMeter("$cachePerfStatsPrefix.query.retries.count", "Total number of times an OQL Query on a Partitioned Region had to be retried", meterTagDefaultArray)

    val regionTransactionCommitMeter = CounterStatisticMeter("$cachePerfStatsPrefix.transaction.commit.count", "Total number times a transaction commit has succeeded.", meterTagDefaultArray)
    val regionTransactionCommitChangesMeter = CounterStatisticMeter("$cachePerfStatsPrefix.transaction.commit.changes", "Total number of changes made by committed transactions.", meterTagDefaultArray)
    val regionTransactionCommitTimer = TimerStatisticMeter("$cachePerfStatsPrefix.transaction.commit.time", "The total amount of time, in nanoseconds, spent doing successful transaction commits.", meterTagDefaultArray, unit = "nanoseconds")
    val regionTransactionCommitSuccessTimer = TimerStatisticMeter("$cachePerfStatsPrefix.transaction.open.time", "The total amount of time, in nanoseconds, spent in a transaction before a successful commit. The time measured starts at transaction begin and ends when commit is called.", meterTagDefaultArray.plus(arrayOf("commitState", "success")), unit = "nanoseconds")
    val regionTransactionCommitFailedTimer = TimerStatisticMeter("$cachePerfStatsPrefix.transaction.open.time", "The total amount of time, in nanoseconds, spent in a transaction before a failed commit. The time measured starts at transaction begin and ends when commit is called.", meterTagDefaultArray.plus(arrayOf("commitState", "failed")), unit = "nanoseconds")
    val regionTransactionFailureMeter = CounterStatisticMeter("$cachePerfStatsPrefix.transaction.commit.count", "Total number times a transaction commit has failed.", meterTagDefaultArray.plus(arrayOf("commitState", "failed")))
    val regionTransactionFailureChangesMeter = CounterStatisticMeter("$cachePerfStatsPrefix.transaction.commit.changes", "Total number of changes lost by failed transactions.", meterTagDefaultArray.plus(arrayOf("commitState", "failed")), "changes")
    val regionTransactionFailureTimer = TimerStatisticMeter("$cachePerfStatsPrefix.transaction.commit.time", "The total amount of time, in nanoseconds, spent doing failed transaction commits.", meterTagDefaultArray.plus(arrayOf("commitState", "failed")), unit = "nanoseconds")
    val regionTransactionRollbackMeter = CounterStatisticMeter("$cachePerfStatsPrefix.transaction.rollback.count", "Total number times a transaction has been explicitly rolled back.", meterTagDefaultArray, "rollbacks")
    val regionTransactionRollbackChangesMeter = CounterStatisticMeter("$cachePerfStatsPrefix.transaction.rollback.changes", "Total number of changes lost by explicit transaction rollbacks.", meterTagDefaultArray, "changes")
    val regionTransactionRollbackTimer = TimerStatisticMeter("$cachePerfStatsPrefix.transaction.rollback.time", "The total amount of time, in nanoseconds, spent doing explicit transaction rollbacks.", meterTagDefaultArray, unit = "nanoseconds")
    val regionOpenTransactionRollbackTimer = TimerStatisticMeter("$cachePerfStatsPrefix.transaction.open.time", "The total amount of time, in nanoseconds, spent in a transaction before an explicit rollback. The time measured starts at transaction begin and ends when rollback is called.", meterTagDefaultArray.plus(arrayOf("commitState", "rollback")), unit = "nanoseconds")
    val regionTransactionConflictCheckTimer = TimerStatisticMeter("$cachePerfStatsPrefix.transaction.conflictcheck.time", "The total amount of time, in nanoseconds, spent doing conflict checks during transaction commit", meterTagDefaultArray, unit = "nanoseconds")

    val regionReliableQueuedOperationsMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.reliable.queued.operations.count", "Current number of cache operations queued for distribution to required roles.", meterTagDefaultArray)
    val regionReliableQueuedOperationsBytesMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.reliable.queued.operations.bytes", "Current size in megabytes of disk used to queue for distribution to required roles.", meterTagDefaultArray, unit = "megabytes")
    val regionReliableRegionMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.reliable.queues.$cachePerfStatsPrefix.count", "Current number of regions configured for reliability.", meterTagDefaultArray)
    val regionReliableRegionMissingMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.reliable.queues.$cachePerfStatsPrefix.missing", "Current number regions configured for reliability that are missing required roles.", meterTagDefaultArray)
    val regionReliableRegionQueuingMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.reliable.queues.$cachePerfStatsPrefix.queuing", "Current number regions configured for reliability that are queuing for required roles.", meterTagDefaultArray)
    val regionReliableRegionMissingFullAccessMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.reliable.queues.$cachePerfStatsPrefix.missing.access", "Current number of regions configured for reliablity that are missing require roles with full access", meterTagDefaultArray.plus(arrayOf("access", "full")))
    val regionReliableRegionMissingLimitedAccessMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.reliable.queues.$cachePerfStatsPrefix.missing.access", "Current number of regions configured for reliablity that are missing required roles with Limited access", meterTagDefaultArray.plus(arrayOf("access", "full")))
    val regionReliableRegionMissingNoAccessMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.reliable.queues.$cachePerfStatsPrefix.missing.access", "Current number of regions configured for reliablity that are missing required roles with No access", meterTagDefaultArray.plus(arrayOf("access", "none")))

    val regionEntryCounterMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.entries", "Current number of entries in the cache. This does not include any entries that are tombstones. See tombstoneCount.", meterTagDefaultArray)
    val regionEventsQueuedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.linked.events.queued", "Number of events attached to other events for callback invocation", meterTagDefaultArray)
    val regionOperationRetryMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operation.retries.count", "Number of times a concurrent destroy followed by a create has caused an entry operation to need to retry.", meterTagDefaultArray)
    val regionClearMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.count", "The total number of times a clear has been done on this cache.", meterTagDefaultArray.plus(arrayOf("operationType", "clear")))

    val diskTasksWaitingMeter = GaugeStatisticMeter("disk.tasks.waiting", "Current number of disk tasks (oplog compactions, asynchronous recoveries, etc) that are waiting for a thread to run the operation", meterTagDefaultArray)
    val regionConflatedEventsMeter = CounterStatisticMeter("$cachePerfStatsPrefix.events.conflated.count", "Number of events not delivered due to conflation.  Typically this means that the event arrived after a later event was already applied to the cache.", meterTagDefaultArray)
    val regionTombstoneEntryMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.entries.tombstones", "Number of destroyed entries that are retained for concurrent modification detection", meterTagDefaultArray)
    val regionTombstoneEntryGCMeter = CounterStatisticMeter("$cachePerfStatsPrefix.entries.tombstones.gc", "Number of garbage-collections performed on destroyed entries", meterTagDefaultArray)
    val regionReplicatedTombstoneBytesMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.tombstones.replicated.bytes", "Amount of memory consumed by destroyed entries in replicated or partitioned regions", meterTagDefaultArray, unit = "bytes")
    val regionNonReplicatedTombstoneBytesMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.tombstones.nonreplicated.bytes", "Amount of memory consumed by destroyed entries in non-replicated regions", meterTagDefaultArray, unit = "bytes")
    val regionOperationClearTimeoutMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operation.clear.timeout", "Number of timeouts waiting for events concurrent to a clear() operation to be received and applied before performing the clear()", meterTagDefaultArray)
    val regionEvictionJobsStartedMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.eviction.job", "Number of evictor jobs started", meterTagDefaultArray.plus(arrayOf("status", "started")))
    val regionEvictionJobsCompletedMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.eviction.job", "Number of evictor jobs completed", meterTagDefaultArray.plus(arrayOf("status", "completed")))
    val regionEvictionQueueSizeMeter = GaugeStatisticMeter("$cachePerfStatsPrefix.eviction.queue.size", "Number of jobs waiting to be picked up by evictor threads", meterTagDefaultArray)
    val regionEvictionTimer = TimerStatisticMeter("$cachePerfStatsPrefix.eviction.time", "Total time spent doing eviction work in background threads", meterTagDefaultArray, unit = "nanoseconds")

    val regionNonSingleHopCountMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operation.nonsingle.hop.count", "Total number of times client request observed more than one hop during operation.", meterTagDefaultArray)
    val regionMetaDataRefreshCountMeter = CounterStatisticMeter("$cachePerfStatsPrefix.metadata.refresh.count", "Total number of times the meta data is refreshed due to hopping observed.", meterTagDefaultArray)

    val regionDeltaUpdateMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.delta.count", "The total number of times entries in this cache are updated through delta bytes.", meterTagDefaultArray.plus(arrayOf("operationType", "update")))
    val regionDeltaUpdateTimer = TimerStatisticMeter("$cachePerfStatsPrefix.operations.delta.time", "Total time spent applying the received delta bytes to entries in this cache.", meterTagDefaultArray.plus(arrayOf("operationType", "update")), unit = "nanoseconds")
    val regionDeltaUpdateFailedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.delta.failed", "The total number of times entries in this cache failed to be updated through delta bytes.", meterTagDefaultArray.plus(arrayOf("operationType", "update")))
    val regionDeltaPreparedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.delta.prepared", "The total number of times delta was prepared in this cache.", meterTagDefaultArray)
    val regionDeltaPreparedTimer = TimerStatisticMeter("$cachePerfStatsPrefix.operations.delta.prepared.time", "Total time spent preparing delta bytes in this cache.", meterTagDefaultArray, unit = "nanoseconds")
    val regionDeltaSentMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.delta.sent", "The total number of times delta was sent to remote caches. This excludes deltas sent from server to client.", meterTagDefaultArray)
    val regionDeltaSentFullValuesMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operations.delta.sent.full", "The total number of times a full value was sent to a remote cache.", meterTagDefaultArray)
    val regionDeltaFullValuesRequestedMeter = CounterStatisticMeter("$cachePerfStatsPrefix.operatations.delta.request.full", "The total number of times a full value was requested by this cache.", meterTagDefaultArray)
    val regionImportedEntryMeter = CounterStatisticMeter("$cachePerfStatsPrefix.entries.imported.count", "The total number of entries imported from a snapshot file.", meterTagDefaultArray, "entries")
    val regionImportedEntryTimer = TimerStatisticMeter("$cachePerfStatsPrefix.entries.imported.time", "The total time spent importing entries from a snapshot file.", meterTagDefaultArray, unit = "nanoseconds")
    val regionExportedEntryMeter = CounterStatisticMeter("$cachePerfStatsPrefix.entries.exported.count", "The total number of entries exported into a snapshot file.", meterTagDefaultArray, "entries")
    val regionExportedEntryTimer = TimerStatisticMeter("$cachePerfStatsPrefix.entries.exported.time", "The total time spent exporting entries into a snapshot file.", meterTagDefaultArray, unit = "nanoseconds")
    val regionEntryCompressTimer = TimerStatisticMeter("$cachePerfStatsPrefix.entries.compress.time", "The total time spent compressing data.", meterTagDefaultArray, unit = "nanoseconds")
    val regionEntryDecompressTimer = TimerStatisticMeter("$cachePerfStatsPrefix.entries.decompress.time", "The total time spent decompressing data.", meterTagDefaultArray, unit = "nanoseconds")
    val regionEntryCompressMeter = CounterStatisticMeter("$cachePerfStatsPrefix.entries.compress.count", "The total number of compression operations.", meterTagDefaultArray)
    val regionEntryDecompressMeter = CounterStatisticMeter("$cachePerfStatsPrefix.entries.decompress.count", "The total number of decompression operations.", meterTagDefaultArray)
    val regionCompressionBeforeBytesMeter = CounterStatisticMeter("$cachePerfStatsPrefix.entries.compress.before.bytes", "The total number of bytes before compressing.", meterTagDefaultArray, "bytes")
    val regionCompressionAfterBytesMeter = CounterStatisticMeter("$cachePerfStatsPrefix.entries.compress.after.bytes", "The total number of bytes after compressing.", meterTagDefaultArray, "bytes")
    val regionEvictionByCriteriaMeter = CounterStatisticMeter("$cachePerfStatsPrefix.evition.criteria.count", "The total number of entries evicted", meterTagDefaultArray, "operations")
    val regionEvictionByCriteriaTimer = TimerStatisticMeter("$cachePerfStatsPrefix.evition.criteria.time", "Time taken for eviction process", meterTagDefaultArray, "nanoseconds")
    val regionEvictionByCriteriaInProgressMeter = CounterStatisticMeter("$cachePerfStatsPrefix.evition.criteria.inprogress", "Total number of evictions in progress", meterTagDefaultArray, "operations")
    val regionEvictionByCriteriaEvaluationMeter = CounterStatisticMeter("$cachePerfStatsPrefix.evition.criteria.evaluation.count", "Total number of evaluations for eviction", meterTagDefaultArray, "operations")
    val regionEvictionByCriteriaEvaluationTimer = TimerStatisticMeter("$cachePerfStatsPrefix.evition.criteria.evaluation.time", "Total time taken for evaluation of user expression during eviction", meterTagDefaultArray, "nanoseconds")

    override open fun initializeStaticMeters() {
        registerMeter(regionLoadsInProgressMeter)
        registerMeter(regionLoadsCompletedMeter)
        registerMeter(regionLoadsTimer)
        registerMeter(regionNetLoadInProgressMeter)
        registerMeter(regionNetLoadCompletedMeter)
        registerMeter(regionNetLoadTimer)
        registerMeter(regionNetSearchInProgressMeter)
        registerMeter(regionNetSearchCompletedMeter)
        registerMeter(regionNetSearchTimer)
        registerMeter(regionCacheWriterInProgressMeter)
        registerMeter(regionCacheWriterCompletedMeter)
        registerMeter(regionCacheWriteTimer)

        registerMeter(regionCacheListenerInProgressMeter)
        registerMeter(regionCacheListenerCompletedMeter)
        registerMeter(regionCacheListenerTimer)
        registerMeter(regionIndexUpdateInProgressMeter)
        registerMeter(regionIndexUpdateCompletedMeter)
        registerMeter(regionIndexUpdateTimeMeter)
        registerMeter(regionIndexInitializationInProgressMeter)
        registerMeter(regionIndexInitializationCompletedMeter)
        registerMeter(regionIndexInitializationTimeMeter)
        registerMeter(regionGIIInProgressMeter)
        registerMeter(regionGIICompletedMeter)
        registerMeter(regionDeltaGIICompletedMeter)
        registerMeter(regionGIITimer)
        registerMeter(regionGIIKeysReceivedMeter)
        registerMeter(numberOfRegionsMeter)
        registerMeter(numberOfPartitionedRegionsMeter)
        registerMeter(regionDestroyOperationMeter)
        registerMeter(regionUpdateOperationMeter)
        registerMeter(regionUpdateOperationTimer)
        registerMeter(regionInvalidateOperationMeter)
        registerMeter(regionGetOperationMeter)
        registerMeter(regionGetOperationTimer)
        registerMeter(regionMissesMeter)
        registerMeter(regionCreateOperationMeter)
        registerMeter(regionPutOperationMeter)
        registerMeter(regionPutOperationTimer)
        registerMeter(regionPutAllOperationMeter)
        registerMeter(regionPutAllOperationTimer)
        registerMeter(regionRemoveAllOperationMeter)
        registerMeter(regionRemoveAllOperationTimer)

        registerMeter(regionEventQueueSizeMeter)
        registerMeter(regionEventQueueThrottleMeter)
        registerMeter(regionEventQueueThrottleTimer)
        registerMeter(regionEventQueueThreadMeter)

        registerMeter(regionQueryExecutionMeter)
        registerMeter(regionQueryExecutionTimer)
        registerMeter(regionQueryResultsHashCollisionsMeter)
        registerMeter(regionQueryResultsHashCollisionsTimer)
        registerMeter(regionQueryExecutionRetryMeter)

        registerMeter(regionTransactionCommitMeter)
        registerMeter(regionTransactionCommitChangesMeter)
        registerMeter(regionTransactionCommitTimer)
        registerMeter(regionTransactionCommitSuccessTimer)
        registerMeter(regionTransactionCommitFailedTimer)
        registerMeter(regionTransactionFailureMeter)
        registerMeter(regionTransactionFailureChangesMeter)
        registerMeter(regionTransactionFailureTimer)
        registerMeter(regionTransactionRollbackMeter)
        registerMeter(regionTransactionRollbackChangesMeter)
        registerMeter(regionTransactionRollbackTimer)
        registerMeter(regionOpenTransactionRollbackTimer)
        registerMeter(regionTransactionConflictCheckTimer)

        registerMeter(regionReliableQueuedOperationsMeter)
        registerMeter(regionReliableQueuedOperationsBytesMeter)
        registerMeter(regionReliableRegionMeter)
        registerMeter(regionReliableRegionMissingMeter)
        registerMeter(regionReliableRegionQueuingMeter)
        registerMeter(regionReliableRegionMissingFullAccessMeter)
        registerMeter(regionReliableRegionMissingLimitedAccessMeter)
        registerMeter(regionReliableRegionMissingNoAccessMeter)

        registerMeter(regionEntryCounterMeter)
        registerMeter(regionEventsQueuedMeter)
        registerMeter(regionOperationRetryMeter)
        registerMeter(regionClearMeter)

        registerMeter(diskTasksWaitingMeter)
        registerMeter(regionConflatedEventsMeter)
        registerMeter(regionTombstoneEntryMeter)
        registerMeter(regionTombstoneEntryGCMeter)
        registerMeter(regionReplicatedTombstoneBytesMeter)
        registerMeter(regionNonReplicatedTombstoneBytesMeter)
        registerMeter(regionOperationClearTimeoutMeter)
        registerMeter(regionEvictionJobsStartedMeter)
        registerMeter(regionEvictionJobsCompletedMeter)
        registerMeter(regionEvictionQueueSizeMeter)
        registerMeter(regionEvictionTimer)

        registerMeter(regionNonSingleHopCountMeter)
        registerMeter(regionMetaDataRefreshCountMeter)

        registerMeter(regionDeltaUpdateMeter)
        registerMeter(regionDeltaUpdateTimer)
        registerMeter(regionDeltaUpdateFailedMeter)
        registerMeter(regionDeltaPreparedMeter)
        registerMeter(regionDeltaPreparedTimer)
        registerMeter(regionDeltaSentMeter)
        registerMeter(regionDeltaSentFullValuesMeter)
        registerMeter(regionDeltaFullValuesRequestedMeter)
        registerMeter(regionImportedEntryMeter)
        registerMeter(regionImportedEntryTimer)
        registerMeter(regionExportedEntryMeter)
        registerMeter(regionExportedEntryTimer)
        registerMeter(regionEntryCompressTimer)
        registerMeter(regionEntryDecompressTimer)
        registerMeter(regionEntryCompressMeter)
        registerMeter(regionEntryDecompressMeter)
        registerMeter(regionCompressionBeforeBytesMeter)
        registerMeter(regionCompressionAfterBytesMeter)
        registerMeter(regionEvictionByCriteriaMeter)
        registerMeter(regionEvictionByCriteriaTimer)
        registerMeter(regionEvictionByCriteriaInProgressMeter)
        registerMeter(regionEvictionByCriteriaEvaluationMeter)
        registerMeter(regionEvictionByCriteriaEvaluationTimer)
    }

    open fun incReliableQueuedOps(inc: Int) {
        regionReliableQueuedOperationsMeter.increment(inc)
    }

    open fun incReliableQueueSize(inc: Int) {
        regionReliableQueuedOperationsBytesMeter.increment(inc)
    }

    open fun incReliableRegions(inc: Int) {
        regionReliableRegionMeter.increment(inc)
    }

    open fun incReliableRegionsMissing(inc: Int) {
        regionReliableRegionMissingMeter.increment(inc)
    }

    open fun incReliableRegionsQueuing(inc: Int) {
        regionReliableRegionQueuingMeter.increment(inc)
    }

    open fun incReliableRegionsMissingFullAccess(inc: Int) {
        regionReliableRegionMissingFullAccessMeter.increment(inc)
    }

    open fun incReliableRegionsMissingLimitedAccess(inc: Int) {
        regionReliableRegionMissingLimitedAccessMeter.increment(inc)
    }

    open fun incReliableRegionsMissingNoAccess(inc: Int) {
        regionReliableRegionMissingNoAccessMeter.increment(inc)
    }

    open fun incQueuedEvents(inc: Int) {
        regionEventsQueuedMeter.increment(inc)
    }

    open fun startCompression(): Long {
        regionEntryCompressMeter.increment()
        return NOW_NANOS
    }

    open fun endCompression(startTime: Long, startSize: Long, endSize: Long) {
        regionEntryCompressTimer.recordValue((NOW_NANOS - startTime))
        regionCompressionBeforeBytesMeter.increment(startSize)
        regionCompressionAfterBytesMeter.increment(endSize)
    }

    open fun startDecompression(): Long {
        regionEntryDecompressMeter.increment()
        return NOW_NANOS
    }

    open fun endDecompression(startTime: Long) {
        regionEntryDecompressTimer.recordValue(NOW_NANOS - startTime)
    }

    open fun startLoad(): Long {
        regionLoadsInProgressMeter.increment()
        return NOW_NANOS
    }

    open fun endLoad(start: Long) {
        regionLoadsTimer.recordValue(NOW_NANOS - start)
        regionLoadsInProgressMeter.decrement()
        regionLoadsCompletedMeter.increment()
    }

    open fun startNetload(): Long {
        regionNetLoadInProgressMeter.increment()
        return NOW_NANOS
    }

    open fun endNetload(start: Long) {
        regionNetLoadTimer.recordValue(NOW_NANOS - start)
        regionNetLoadInProgressMeter.decrement()
        regionNetLoadCompletedMeter.increment()
    }

    open fun startNetsearch(): Long {
        regionNetSearchInProgressMeter.increment()
        return NOW_NANOS
    }

    open fun endNetsearch(start: Long) {
        regionNetSearchTimer.recordValue(NOW_NANOS - start)
        regionNetSearchInProgressMeter.decrement()
        regionNetSearchCompletedMeter.increment()
    }

    open fun startCacheWriterCall(): Long {
        regionCacheWriterInProgressMeter.increment()
        return NOW_NANOS
    }

    open fun endCacheWriterCall(start: Long) {
        regionCacheWriteTimer.recordValue(NOW_NANOS - start)
        regionCacheWriterCompletedMeter.increment()
        regionCacheWriterInProgressMeter.decrement()
    }

    open fun startCacheListenerCall(): Long {
        regionCacheListenerInProgressMeter.increment()
        return NOW_NANOS
    }

    open fun endCacheListenerCall(start: Long) {
        regionCacheListenerTimer.recordValue(NOW_NANOS - start)
        regionCacheListenerInProgressMeter.decrement()
        regionCacheListenerCompletedMeter.increment()
    }

    open fun startGetInitialImage(): Long {
        regionGIIInProgressMeter.increment()
        return NOW_NANOS
    }

    open fun endGetInitialImage(start: Long) {
        regionGIITimer.recordValue(NOW_NANOS - start)
        regionGIIInProgressMeter.decrement()
        regionGIICompletedMeter.increment()
    }

    open fun endNoGIIDone(start: Long) {
        regionGIITimer.recordValue(NOW_NANOS - start)
        regionGIIInProgressMeter.decrement()
    }

    open fun incDeltaGIICompleted() {
        regionDeltaGIICompletedMeter.increment()
    }

    open fun incGetInitialImageKeysReceived() {
        regionGIIKeysReceivedMeter.increment()
    }

    open fun startIndexUpdate(): Long {
        regionIndexUpdateInProgressMeter.increment()
        return NOW_NANOS
    }

    open fun endIndexUpdate(start: Long) {
        regionIndexUpdateTimeMeter.recordValue(NOW_NANOS - start)
        regionIndexUpdateInProgressMeter.decrement()
        regionIndexUpdateCompletedMeter.increment()
    }

    open fun startIndexInitialization(): Long {
        regionIndexInitializationInProgressMeter.increment()
        return NOW_NANOS
    }

    open fun endIndexInitialization(start: Long) {
        regionIndexInitializationTimeMeter.recordValue(NOW_NANOS - start)
        regionIndexInitializationInProgressMeter.decrement()
        regionIndexInitializationCompletedMeter.increment()
    }

    open fun incRegions(inc: Int) {
        numberOfRegionsMeter.increment(inc)
    }

    open fun incPartitionedRegions(inc: Int) {
        numberOfPartitionedRegionsMeter.increment(inc)
    }

    open fun incDestroys() {
        regionDestroyOperationMeter.increment()
    }

    open fun incCreates() {
        regionCreateOperationMeter.increment()
    }

    open fun incInvalidates() {
        regionInvalidateOperationMeter.increment()
    }

    open fun startGet(): Long {
        return NOW_NANOS
    }

    open fun endGet(start: Long, miss: Boolean) {
        regionGetOperationTimer.recordValue(NOW_NANOS - start)
        regionGetOperationMeter.increment()
        if (miss) {
            regionMissesMeter.increment()
        }
    }

    open fun endPut(start: Long, isUpdate: Boolean): Long {
        var total: Long
        if (isUpdate) {
            regionUpdateOperationMeter.increment()
            total = NOW_NANOS - start
            regionUpdateOperationTimer.recordValue(total)
        } else {
            regionPutOperationMeter.increment()
            total = NOW_NANOS - start
            regionPutOperationTimer.recordValue(total)
        }
        return total
    }

    open fun endPutAll(start: Long) {
        regionPutAllOperationMeter.increment()
        regionPutAllOperationTimer.recordValue(NOW_NANOS - start)
    }

    open fun endRemoveAll(start: Long) {
        regionRemoveAllOperationMeter.increment()
        regionRemoveAllOperationTimer.recordValue(NOW_NANOS - start)
    }

    open fun endQueryExecution(executionTime: Long) {
        regionQueryExecutionMeter.increment()
        regionQueryExecutionTimer.recordValue(executionTime)
    }

    open fun endQueryResultsHashCollisionProbe(start: Long) {
        regionQueryResultsHashCollisionsTimer.recordValue(NOW_NANOS - start)
    }

    open fun incQueryResultsHashCollisions() {
        regionQueryResultsHashCollisionsMeter.increment()
    }

    open fun incTxConflictCheckTime(delta: Long) {
        regionTransactionConflictCheckTimer.recordValue(delta)
    }

    open fun txSuccess(opTime: Long, txLifeTime: Long, txChanges: Int) {
        regionTransactionCommitMeter.increment()
        regionTransactionCommitChangesMeter.increment(txChanges)
        regionTransactionCommitTimer.recordValue(opTime)
        regionTransactionCommitSuccessTimer.recordValue(txLifeTime)
    }

    open fun txFailure(opTime: Long, txLifeTime: Long, txChanges: Int) {
        regionTransactionFailureMeter.increment()
        regionTransactionFailureChangesMeter.increment(txChanges)
        regionTransactionFailureTimer.recordValue(opTime)
        regionTransactionCommitFailedTimer.recordValue(txLifeTime)
    }

    open fun txRollback(opTime: Long, txLifeTime: Long, txChanges: Int) {
        regionTransactionRollbackMeter.increment()
        regionTransactionRollbackChangesMeter.increment(txChanges)
        regionTransactionRollbackTimer.recordValue(opTime)
        regionOpenTransactionRollbackTimer.recordValue(txLifeTime)
    }

    open fun endDeltaUpdate(start: Long) {
        regionDeltaUpdateMeter.increment()
        regionDeltaUpdateTimer.recordValue(NOW_NANOS - start)
    }

    open fun incDeltaFailedUpdates() {
        regionDeltaUpdateFailedMeter.increment()
    }

    open fun endDeltaPrepared(start: Long) {
        regionDeltaPreparedMeter.increment()
        regionDeltaPreparedTimer.recordValue(NOW_NANOS - start)
    }

    open fun incDeltasSent() {
        regionDeltaSentMeter.increment()
    }

    open fun incDeltaFullValuesSent() {
        regionDeltaSentFullValuesMeter.increment()
    }

    open fun incDeltaFullValuesRequested() {
        regionDeltaFullValuesRequestedMeter.increment()
    }

    open fun incEventQueueSize(items: Int) {
        regionEventQueueSizeMeter.increment(items)
    }

    open fun incEventQueueThrottleCount(items: Int) {
        regionEventQueueThrottleMeter.increment(items)
    }

    open fun incEventQueueThrottleTime(nanos: Long) {
        regionEventQueueThrottleTimer.recordValue(nanos)
    }

    open fun incEventThreads(items: Int) {
        regionEventQueueThreadMeter.increment(items)
    }

    open fun incEntryCount(delta: Int) {
        regionEntryCounterMeter.increment(delta)
    }

    open fun incRetries() {
        regionOperationRetryMeter.increment()
    }

    open fun incDiskTasksWaiting() {
        diskTasksWaitingMeter.increment()
    }

    open fun decDiskTasksWaiting() {
        diskTasksWaitingMeter.decrement()
    }

    open fun decDiskTasksWaiting(count: Int) {
        diskTasksWaitingMeter.decrement(count)
    }

    open fun incEvictorJobsStarted() {
        regionEvictionJobsStartedMeter.increment()
    }

    open fun incEvictorJobsCompleted() {
        regionEvictionJobsCompletedMeter.increment()
    }

    open fun incEvictorQueueSize(delta: Int) {
        regionEvictionQueueSizeMeter.increment(delta)
    }

    open fun incEvictWorkTime(delta: Long) {
        regionEvictionTimer.recordValue(delta)
    }

    open fun getEventPoolHelper(): PoolStatHelper {
        return object : PoolStatHelper {
            override fun startJob() {
                incEventThreads(1)
            }

            override fun endJob() {
                incEventThreads(-1)
            }
        }
    }

    open fun incClearCount() {
        regionClearMeter.increment()
    }

    open fun incConflatedEventsCount() {
        regionConflatedEventsMeter.increment()
    }

    open fun incTombstoneCount(amount: Int) {
        regionTombstoneEntryMeter.increment(amount)
    }

    open fun incTombstoneGCCount() {
        regionTombstoneEntryGCMeter.increment()
    }

    open fun setReplicatedTombstonesSize(size: Long) {
        regionReplicatedTombstoneBytesMeter.increment(size)
    }

    open fun setNonReplicatedTombstonesSize(size: Long) {
        regionNonReplicatedTombstoneBytesMeter.increment(size)
    }

    open fun incClearTimeouts() {
        regionOperationClearTimeoutMeter.increment()
    }

    open fun incPRQueryRetries() {
        regionQueryExecutionRetryMeter.increment()
    }

    open fun getEvictionQueueStatHelper(): QueueStatHelper {
        return object : QueueStatHelper {
            override fun add() {
                incEvictorQueueSize(1)
            }

            override fun remove() {
                incEvictorQueueSize(-1)
            }

            override fun remove(count: Int) {
                incEvictorQueueSize(count * -1)
            }
        }
    }

    open fun incMetaDataRefreshCount() {
        regionMetaDataRefreshCountMeter.increment()
    }

    open fun endImport(entryCount: Long, start: Long) {
        regionImportedEntryMeter.increment(entryCount)
        regionImportedEntryTimer.recordValue(NOW_NANOS - start)
    }

    open fun endExport(entryCount: Long, start: Long) {
        regionExportedEntryMeter.increment(entryCount)
        regionExportedEntryTimer.recordValue(NOW_NANOS - start)
    }

    @Deprecated(message = "This was just done as an interim solution until GEODE does not depend on stats for internal state")
    fun getReliableRegionsMissing(): Int = regionReliableRegionMissingMeter.getValue().toInt()

    @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
    fun getNetsearchesCompleted(): Long = regionNetLoadCompletedMeter.getValue()

    @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
    fun getEventQueueSize(): Long = regionEventQueueSizeMeter.getValue()

    @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
    fun getLoadsCompleted(): Long = regionLoadsCompletedMeter.getValue()

    @Deprecated("The method is deprecated to be removed, but here until a better stats mechanism is found")
    fun getGets(): Long = regionGetOperationMeter.getValue()


}
