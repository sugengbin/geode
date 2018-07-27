package org.apache.geode.statistics.distributed

interface DMStats {

    fun incSentMessages(messages: Long)

    fun incTOSentMsg()

    fun incSentCommitMessages(messages: Long)

    fun incCommitWaits()

    fun incSentMessagesTime(nanos: Long)

    fun incBroadcastMessages(messages: Long)

    fun incBroadcastMessagesTime(nanos: Long)

    fun incReceivedMessages(messages: Long)

    fun incReceivedBytes(bytes: Long)

    fun incSentBytes(bytes: Long)

    fun incProcessedMessages(messages: Long)

    fun incProcessedMessagesTime(nanos: Long)

    fun incBatchSendTime(start: Long)

    fun incBatchCopyTime(start: Long)

    fun incBatchWaitTime(start: Long)

    fun incBatchFlushTime(start: Long)

    fun incMessageProcessingScheduleTime(nanos: Long)

    fun incOverflowQueueSize(messages: Int)

    fun incNumProcessingThreads(threads: Int)

    fun incNumSerialThreads(threads: Int)

    fun incMessageChannelTime(`val`: Long)

    fun incUDPDispatchRequestTime(`val`: Long)

    fun incReplyMessageTime(`val`: Long)

    fun incDistributeMessageTime(`val`: Long)

    fun startSocketWrite(sync: Boolean): Long

    fun endSocketWrite(sync: Boolean, start: Long, bytesWritten: Int, retries: Int)

    fun incUcastWriteBytes(bytesWritten: Int)

    fun incUcastReadBytes(amount: Int)

    fun incMcastWriteBytes(bytesWritten: Int)

    fun incMcastReadBytes(amount: Int)

    fun startSerialization(): Long

    fun endSerialization(start: Long, bytes: Int)

    fun startDeserialization(): Long

    fun endDeserialization(start: Long, bytes: Int)

    fun startMsgSerialization(): Long

    fun endMsgSerialization(start: Long)

    fun startUDPMsgEncryption(): Long

    fun endUDPMsgEncryption(start: Long)

    fun startUDPMsgDecryption(): Long

    fun endUDPMsgDecryption(start: Long)

    fun startMsgDeserialization(): Long

    fun endMsgDeserialization(start: Long)

    fun setNodes(`val`: Int)

    fun incNodes(`val`: Int)

    fun startReplyWait(): Long

    fun endReplyWait(startNanos: Long, initTime: Long)

    fun incReplyTimeouts()

    fun incReceivers()

    fun decReceivers()

    fun incFailedAccept()

    fun incFailedConnect()

    fun incReconnectAttempts()

    fun incLostLease()

    fun incSenders(shared: Boolean, preserveOrder: Boolean)

    fun decSenders(shared: Boolean, preserveOrder: Boolean)

    fun incUcastRetransmits()

    fun incMcastRetransmits()

    fun incMcastRetransmitRequests()

    fun incAsyncQueues(inc: Int)

    fun startAsyncQueueFlush(): Long

    fun endAsyncQueueFlush(start: Long)

    fun incAsyncQueueTimeouts(inc: Int)

    fun incAsyncQueueSizeExceeded(inc: Int)

    fun incAsyncDistributionTimeoutExceeded()

    fun incAsyncQueueSize(inc: Long)

    fun incAsyncQueuedMsgs()

    fun incAsyncDequeuedMsgs()

    fun incAsyncConflatedMsgs()

    fun incAsyncThreads(inc: Int)

    fun startAsyncThread(): Long

    fun endAsyncThread(start: Long)

    fun incAsyncQueueAddTime(inc: Long)

    fun incAsyncQueueRemoveTime(inc: Long)

    fun incReceiverBufferSize(inc: Int, direct: Boolean)

    fun incSenderBufferSize(inc: Int, direct: Boolean)

    fun startSocketLock(): Long

    fun endSocketLock(start: Long)

    fun startBufferAcquire(): Long

    fun endBufferAcquire(start: Long)

    fun incThreadOwnedReceivers(value: Long, dominoCount: Int)

    fun incMessagesBeingReceived(newMsg: Boolean, bytes: Int)

    fun decMessagesBeingReceived(bytes: Int)

    fun incReplyHandOffTime(start: Long)

    fun incElders(`val`: Int)

    fun incInitialImageMessagesInFlight(`val`: Int)

    fun incInitialImageRequestsInProgress(`val`: Int)

    fun incPdxSerialization(bytesWritten: Int)

    fun incPdxDeserialization(i: Int)

    fun startPdxInstanceDeserialization(): Long

    fun endPdxInstanceDeserialization(start: Long)

    fun incPdxInstanceCreations()

    fun incHeartbeatRequestsSent()

    fun incHeartbeatRequestsReceived()

    fun incHeartbeatsSent()

    fun incHeartbeatsReceived()

    fun incSuspectsSent()

    fun incSuspectsReceived()

    fun incFinalCheckRequestsSent()

    fun incFinalCheckRequestsReceived()

    fun incFinalCheckResponsesSent()

    fun incFinalCheckResponsesReceived()

    fun incTcpFinalCheckRequestsSent()

    fun incTcpFinalCheckRequestsReceived()

    fun incTcpFinalCheckResponsesSent()

    fun incTcpFinalCheckResponsesReceived()

    fun incUdpFinalCheckRequestsSent()

    fun incUdpFinalCheckResponsesReceived()
}

