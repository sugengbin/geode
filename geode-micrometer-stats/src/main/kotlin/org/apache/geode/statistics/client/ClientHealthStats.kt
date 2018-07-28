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
package org.apache.geode.statistics.client

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException
import java.io.Serializable
import java.util.Date
import java.util.HashMap
import kotlin.collections.Map.Entry

import org.apache.geode.DataSerializer
import org.apache.geode.internal.DataSerializableFixedID
import org.apache.geode.internal.Version

/**
 * Bean class act as container for client stats
 *
 */

data class ClientHealthStats : DataSerializableFixedID, Serializable {


    var numOfGets: Int = 0

    /**
     * "numOfPuts", IntCounter, "The total number of times an entry is added or replaced in this cache
     * as a result of a local operation (put(), create(), or get() which results in load, netsearch,
     * or netloading a value). Note that this only counts puts done explicitly on this cache. It does
     * not count updates pushed from other caches." Java: CachePerfStats.puts Native: Not yet Defined
     */
    /**
     * This method returns the total number of successful put requests completed.
     *
     * @return Total number of put requests completed.
     */
    /**
     * This method sets the total number of successful put requests completed.
     *
     * @param numOfPuts Total number of put requests to be set.
     */
    var numOfPuts: Int = 0

    /**
     * Represents number of cache misses in this client. IntCounter, "Total number of times a get on
     * the cache did not find a value already in local memory." Java: CachePerfStats.misses
     */
    /**
     * This method returns total number of cache misses in this client.
     *
     * @return total number of cache misses.
     */
    /**
     * This method sets total number of cache misses in this client.
     *
     * @param numOfMisses total number of cache misses.
     */
    var numOfMisses: Int = 0

    /**
     * Represents number of cache listners calls completed. IntCounter, "Total number of times a cache
     * listener call has completed." Java: CachePerfStats.cacheListenerCallsCompleted
     */
    /**
     * This method returns total number of cache listener calls completed.
     *
     * @return total number of cache listener calls completed.
     */
    /**
     * This method sets total number of cache listener calls compeleted.
     *
     * @param numOfCacheListenerCalls total number of cache listener calls completed.
     */
    var numOfCacheListenerCalls: Int = 0

    /**
     * Represents total number of active threads in the client VM. IntCounter, "Current number of live
     * threads (both daemon and non-daemon) in this VM." Java: VMStats.threads
     */
    /**
     * This method returns total number of threads in the client VM.
     *
     * @return total number of threads in the client VM
     */
    /**
     * This method sets the total number of threads in the client VM.
     *
     * @param numOfThreads total number of threads in the client VM
     */
    var numOfThreads: Int = 0

    /**
     * Represents the CPU time used by the process (in nanoseconds). LongCounter, "CPU timed used by
     * the process in nanoseconds." Java: VMStats.processCpuTime
     */
    /**
     * This method returns the CPU time used by the process (in nanoseconds)
     *
     * @return CPU time used by the process (in nanoseconds)
     */
    /**
     * This method sets the CPU time used by the process (in nanoseconds).
     *
     * @param processCpuTime CPU time used by the process (in nanoseconds)
     */
    var processCpuTime: Long = 0

    /**
     * Represents the number of cpus available to the java VM on its machine. IntCounter, "Number of
     * cpus available to the java VM on its machine." Java: VMStats.cpus
     */
    var cpus: Int = 0


    /**
     * Represents time when this snapshot of the client statistics was taken.
     */
    var updateTime: Date

    /**
     * Represents stats for a poolName .
     */
    var poolStats = HashMap<String, String>()

    val dsfid: Int
        get() = DataSerializableFixedID.CLIENT_HEALTH_STATS

    val serializationVersions: Array<Version>
        get() = dsfidVersions

    @Throws(IOException::class)
    fun toData(out: DataOutput) {
        DataSerializer.writePrimitiveInt(numOfGets, out)
        DataSerializer.writePrimitiveInt(numOfPuts, out)
        DataSerializer.writePrimitiveInt(numOfMisses, out)
        DataSerializer.writePrimitiveInt(numOfCacheListenerCalls, out)
        DataSerializer.writePrimitiveInt(numOfThreads, out)
        DataSerializer.writePrimitiveInt(cpus, out)
        DataSerializer.writePrimitiveLong(processCpuTime, out)
        DataSerializer.writeDate(updateTime, out)
        DataSerializer.writeHashMap(poolStats, out)
    }

    @Throws(IOException::class)
    fun toDataPre_GFE_8_0_0_0(out: DataOutput) {
        DataSerializer.writePrimitiveInt(numOfGets, out)
        DataSerializer.writePrimitiveInt(numOfPuts, out)
        DataSerializer.writePrimitiveInt(numOfMisses, out)
        DataSerializer.writePrimitiveInt(numOfCacheListenerCalls, out)
        DataSerializer.writePrimitiveInt(numOfThreads, out)
        DataSerializer.writePrimitiveInt(cpus, out)
        DataSerializer.writePrimitiveLong(processCpuTime, out)
        DataSerializer.writeDate(updateTime, out)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    fun fromData(`in`: DataInput) {
        this.numOfGets = DataSerializer.readPrimitiveInt(`in`)
        this.numOfPuts = DataSerializer.readPrimitiveInt(`in`)
        this.numOfMisses = DataSerializer.readPrimitiveInt(`in`)
        this.numOfCacheListenerCalls = DataSerializer.readPrimitiveInt(`in`)
        this.numOfThreads = DataSerializer.readPrimitiveInt(`in`)
        this.cpus = DataSerializer.readPrimitiveInt(`in`)
        this.processCpuTime = DataSerializer.readPrimitiveLong(`in`)
        this.updateTime = DataSerializer.readDate(`in`)
        this.poolStats = DataSerializer.readHashMap(`in`)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    fun fromDataPre_GFE_8_0_0_0(`in`: DataInput) {
        this.numOfGets = DataSerializer.readPrimitiveInt(`in`)
        this.numOfPuts = DataSerializer.readPrimitiveInt(`in`)
        this.numOfMisses = DataSerializer.readPrimitiveInt(`in`)
        this.numOfCacheListenerCalls = DataSerializer.readPrimitiveInt(`in`)
        this.numOfThreads = DataSerializer.readPrimitiveInt(`in`)
        this.cpus = DataSerializer.readPrimitiveInt(`in`)
        this.processCpuTime = DataSerializer.readPrimitiveLong(`in`)
        this.updateTime = DataSerializer.readDate(`in`)
    }

    override fun toString(): String {

        val buf = StringBuffer()
        buf.append("ClientHealthStats [")
        buf.append("\n numOfGets=" + this.numOfGets)
        buf.append("\n numOfPuts=" + this.numOfPuts)
        buf.append("\n numOfMisses=" + this.numOfMisses)
        buf.append("\n numOfCacheListenerCalls=" + this.numOfCacheListenerCalls)
        buf.append("\n numOfThreads=" + this.numOfThreads)
        buf.append("\n cpus=" + this.cpus)
        buf.append("\n processCpuTime=" + this.processCpuTime)
        buf.append("\n updateTime=" + this.updateTime)
        val it = this.poolStats.entries.iterator()
        val tempBuffer = StringBuffer()
        while (it.hasNext()) {
            val entry = it.next()
            tempBuffer.append(entry.key + " = " + entry.value)
        }
        buf.append("\n poolStats $tempBuffer")
        buf.append("\n]")

        return buf.toString()
    }

    companion object {
        private const val serialVersionUID = 4229401714870332766L

        /** The versions in which this message was modified  */
        private val dsfidVersions = arrayOf<Version>(Version.GFE_80)
    }
}
