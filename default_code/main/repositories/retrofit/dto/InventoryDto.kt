package cu.jaco.transito.repositories.retrofit.dto

import com.google.gson.annotations.SerializedName

data class InventoryDto(
    @SerializedName("pending")
    val pending: Int,
    @SerializedName("available")
    val available: Int,
    @SerializedName("not_available")
    val not_available: Int
)