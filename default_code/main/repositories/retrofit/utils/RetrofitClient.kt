package cu.jaco.transito.repositories.retrofit.utils

import android.os.Build
import com.google.gson.GsonBuilder
import cu.jaco.transito.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

abstract class RetrofitClient<T> {

    private var certificate = CertificateUtils.providerCertificate()
    private var retrofitBuilder = providerRetrofitBuilder()
    private var okHttpBuilder = providerOkHttpBuilder()
    private var retrofit = providerAuthRetrofit(retrofitBuilder, okHttpBuilder)
    private var service: T? = null

    fun invalidate() {
        retrofitBuilder = providerRetrofitBuilder()
        okHttpBuilder = providerOkHttpBuilder()
        retrofit = providerAuthRetrofit(retrofitBuilder, okHttpBuilder)
        service = serviceCreator(retrofit)
    }

    private fun providerOkHttpBuilder(): OkHttpClient.Builder {
        val client = OkHttpClient().newBuilder()
            .connectTimeout(15000L, TimeUnit.MILLISECONDS)
            .readTimeout(15000L, TimeUnit.MILLISECONDS)
            .writeTimeout(15000L, TimeUnit.MILLISECONDS)
            .configSsl(certificate)

        client.addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val headers = headers()

            for (header in headers) {
                requestBuilder.addHeader(header.key, header.value)
            }
            chain.proceed(requestBuilder.build())
        }

        if (BuildConfig.DEBUG)
            client.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })

        return client
    }

    private fun providerRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
    }

    fun serviceCreator(): T {
        if (service == null)
            service = serviceCreator(retrofit)
        return service!!
    }

    private fun providerAuthRetrofit(
        retrofitBuilder: Retrofit.Builder,
        okHttpClientBuilder: OkHttpClient.Builder
    ): Retrofit {

        return retrofitBuilder
            .client(okHttpClientBuilder.build())
            .baseUrl(getBaseUrl())
            .build()
    }

    @Throws(
        CertificateException::class,
        IOException::class,
        KeyStoreException::class,
        NoSuchAlgorithmException::class,
        KeyManagementException::class,
        IllegalStateException::class
    )
    private fun OkHttpClient.Builder.configSsl(ca: Certificate?): OkHttpClient.Builder {

        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)

        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, null)

        if (tmf.trustManagers.size != 1 || tmf.trustManagers[0] == null
            || tmf.trustManagers[0] !is X509TrustManager
        ) {
            throw IllegalStateException(
                "Unexpected default trust managers: ${
                    Arrays.toString(
                        tmf.trustManagers
                    )
                }"
            )
        }
        val trustManager = tmf.trustManagers[0] as X509TrustManager

        if (Build.VERSION.SDK_INT < 22)
            sslSocketFactory(Tls12SocketFactory(sslContext!!.socketFactory), trustManager)
        else
            sslSocketFactory(sslContext!!.socketFactory, trustManager)
        hostnameVerifier { _, _ ->
            true
        }

        return this
    }

    protected abstract fun serviceCreator(retrofit: Retrofit): T

    protected abstract fun getBaseUrl(): String

    protected abstract fun headers(): HashMap<String, String>

}