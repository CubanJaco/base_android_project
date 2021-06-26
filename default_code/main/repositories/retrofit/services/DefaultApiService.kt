package cu.jaco.transito.repositories.retrofit.services

import cu.jaco.transito.repositories.retrofit.api.DefaultApi
import cu.jaco.transito.repositories.retrofit.utils.SafeApiCall
import javax.inject.Inject

class DefaultApiService @Inject constructor(
    private val api: DefaultApi
) : SafeApiCall() {

    suspend fun inventory() = safeApiCall { api.inventory() }

}