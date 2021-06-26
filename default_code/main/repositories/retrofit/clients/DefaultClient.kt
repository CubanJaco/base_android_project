package cu.jaco.transito.repositories.retrofit.clients

import cu.jaco.transito.repositories.retrofit.api.DefaultApi
import cu.jaco.transito.repositories.retrofit.utils.RetrofitClient
import retrofit2.Retrofit
import javax.inject.Inject

class DefaultClient @Inject constructor() : RetrofitClient<DefaultApi>() {

    override fun headers(): HashMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Authorization"] = "eyJ4NXQiOiJOVGRtWmpNNFpEazNOalkwWXpjNU1=="
        return headers
    }

    override fun serviceCreator(retrofit: Retrofit): DefaultApi {
        return retrofit.create(DefaultApi::class.java)
    }

    override fun getBaseUrl(): String = "https://petstore.swagger.io/"

}