package cu.jaco.transito.repositories.retrofit.utils

import retrofit2.Response


sealed class ResultWrapper {
    data class Success<T>(val value: T) : ResultWrapper()
    data class HttpError(val response: Response<*>?) : ResultWrapper()
    data class Error(val exception: Throwable) : ResultWrapper()
}