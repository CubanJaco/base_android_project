package cu.jaco.transito.repositories.retrofit.utils

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Throws(
    CertificateException::class,
    IOException::class,
    KeyStoreException::class,
    NoSuchAlgorithmException::class,
    KeyManagementException::class,
    IllegalStateException::class
)
fun OkHttpClient.Builder.configSsl(ca: Certificate?): OkHttpClient.Builder {

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
        sslSocketFactory(
            Tls12SocketFactory(
                sslContext!!.socketFactory
            ), trustManager
        )
    else
        sslSocketFactory(sslContext!!.socketFactory, trustManager)

    hostnameVerifier(HostnameVerifier { hostname, session -> true })

    return this
}

fun <T> Call<T>.toLiveData(expectedHttpCode: Int = 200): LiveData<ResultWrapper> = liveData {
    emit(suspended(expectedHttpCode))
}

suspend inline fun <T> Call<T>.suspended(expectedHttpCode: Int = 200): ResultWrapper =
    suspendCoroutine { cont ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (expectedHttpCode == response.code()) {
                    val body = response.body()
                    cont.resume(ResultWrapper.Success(body))
                } else {
                    cont.resume(ResultWrapper.HttpError(response))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                cont.resume(ResultWrapper.Error(t))
            }
        })
    }