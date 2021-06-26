package cu.jaco.transito.di

import cu.jaco.transito.repositories.retrofit.api.DefaultApi
import cu.jaco.transito.repositories.retrofit.clients.DefaultClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Provides
    fun providerDefaultClient(client: DefaultClient): DefaultApi {
        return client.serviceCreator()
    }

}