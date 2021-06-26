package cu.jaco.transito.repositories.retrofit.api

import cu.jaco.transito.repositories.retrofit.dto.InventoryDto
import retrofit2.http.GET

interface DefaultApi {

    @GET("v2/store/inventory")
    suspend fun inventory(): InventoryDto

}