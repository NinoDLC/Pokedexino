package fr.delcey.pokedexino.data.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

/**
 * Interpolates the presence of a unique [ID] in a list for an [interpolationDuration] (default is 2 seconds).
 */
open class InterpolationRepository<ID>(
    private val globalScope: CoroutineScope,
    private val interpolationDuration: Duration = 2.seconds,
) {

    private val interpolationId = AtomicLong()
    private val map = mutableMapOf<ID, Pair<Long, Boolean>>()

    private val interpolatedMutableStateFlow = MutableSharedFlow<MutableMap<ID, Pair<Long, Boolean>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).apply {
        tryEmit(map)
    }

    fun add(id: ID): Long = update(id = id, isPresent = true)

    fun remove(id: ID): Long = update(id = id, isPresent = false)

    fun invalidate(interpolationId: Long) {
        map.entries.find { it.value.first == interpolationId }?.let { matchingEntry ->
            map.remove(matchingEntry.key)

            interpolatedMutableStateFlow.tryEmit(map)
        }
    }

    fun interpolatedWithRealData(realDataListFlow: Flow<List<ID>>): Flow<List<ID>> = combine(
        realDataListFlow,
        interpolatedMutableStateFlow
    ) { realDataList: List<ID>, interpolatedDataMap: Map<ID, Pair<Long, Boolean>> ->
        val allIds: Set<ID> = buildSet {
            addAll(realDataList)
            addAll(interpolatedDataMap.keys)
        }

        allIds.filter { id ->
            val interpolatedValue = interpolatedDataMap[id]?.second
            val realValue = realDataList.contains(id)

            interpolatedValue ?: realValue
        }
    }.distinctUntilChanged()

    private fun update(id: ID, isPresent: Boolean): Long {
        val currentInterpolationId = interpolationId.getAndIncrement()

        // Global scope use because if the scope is killed during the delay, the value will always be interpolated...
        globalScope.launch {
            map[id] = Pair(currentInterpolationId, isPresent)
            interpolatedMutableStateFlow.tryEmit(map)

            delay(interpolationDuration)

            if (map[id]?.first == currentInterpolationId) {
                map.remove(id)
                interpolatedMutableStateFlow.tryEmit(map)
            }
        }

        return currentInterpolationId
    }
}