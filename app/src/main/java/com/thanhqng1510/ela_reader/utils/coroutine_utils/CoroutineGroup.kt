package com.thanhqng1510.ela_reader.utils.coroutine_utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.random.Random

/**
 * A group to manage multiple coroutine within a given scope
 *
 * @param scope A coroutine scope referenced from this group to manage and execute coroutines
 */
class CoroutineGroup(private val scope: CoroutineScope) {
    private val coroutinePool = mutableMapOf<Long, Deferred<Any>?>()

    /**
     * Execute a suspend function asynchronously and return its id for later reference
     */
    fun addSuspendableBlock(block: suspend () -> Any): Long {
        val id = getPoolSlotId()
        coroutinePool[id] = scope.async { block() }
        return id
    }

    /**
     * Wait for a specific coroutine to complete
     *
     * @param id Id of the coroutine to wait for completion
     */
    suspend fun wait(id: Long): Any {
        val result = coroutinePool[id]?.await() ?: throw IllegalArgumentException()
        coroutinePool[id] = null
        return result
    }

    /**
     * Wait for all coroutines currently in this group to complete
     *
     * All results will be put a map, identified by each coroutine's
     */
    suspend fun waitAll(): Map<Long, Any> {
        val result = coroutinePool.filter { (_, d) -> d != null }
            .mapValues { (_, deferred) -> deferred!!.await() }
        coroutinePool.clear()
        return result
    }

    private fun getPoolSlotId(): Long {
        var id: Long
        do {
            id = Random.nextLong()
        } while (coroutinePool.containsKey(id) && coroutinePool[id] != null)
        return id
    }
}