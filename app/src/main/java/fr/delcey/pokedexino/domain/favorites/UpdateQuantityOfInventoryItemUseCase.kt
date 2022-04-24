package fr.delcey.pokedexino.domain.favorites

import fr.delcey.myinventory.data.inventory.InterpolatedQuantityRepository
import fr.delcey.myinventory.data.inventory.InventoryRepository
import fr.delcey.myinventory.domain.user.GetLoggedUserUseCase
import fr.delcey.myinventory.ui.utils.loge
import fr.delcey.pokedexino.domain.user.GetLoggedUserUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

// TODO NINO to "like pokemon"
class UpdateQuantityOfInventoryItemUseCase @Inject constructor(
    private val getLoggedUserUseCase: GetLoggedUserUseCase,
    private val inventoryRepository: InventoryRepository,
    private val interpolatedQuantityRepository: InterpolatedQuantityRepository,
) {

    private val updateQuantityJobs = mutableMapOf<String, Job>()

    suspend operator fun invoke(inventoryId: String?, inventoryItemId: String, newQuantity: Float): Boolean = try {
        coroutineScope {
            interpolatedQuantityRepository.put(inventoryItemId, newQuantity)

            withTimeoutOrNull(3_000) {
                val updateQuantityJob = launch {
                    val userId = getLoggedUserUseCase.invoke().first { it?.uid != null }?.uid

                    if (userId != null) {
                        delay(1_000) // Avoid extra write cost !
                        inventoryRepository.updateQuantity(userId, inventoryId, inventoryItemId, newQuantity)
                    } else {
                        throw IllegalStateException("UserId is null !")
                    }
                }

                val previous = updateQuantityJobs.put(inventoryItemId, updateQuantityJob)
                previous?.cancel()

                true
            } ?: false
        }
    } catch (e: Exception) {
        loge(e)
        false
    }
}