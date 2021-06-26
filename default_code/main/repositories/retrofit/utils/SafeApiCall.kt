package cu.jaco.transito.repositories.retrofit.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

open class SafeApiCall(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    /**
     * @param apiCall Funcion a ejecutar en la API
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): ResultWrapper {
        return request(apiCall)
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> request(
        apiCall: suspend () -> T
    ): ResultWrapper {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                when (throwable) {
                    is HttpException -> {
                        ResultWrapper.HttpError(throwable.response())
                    }
                    else -> {
                        ResultWrapper.Error(throwable)
                    }
                }
            }
        }
    }

}