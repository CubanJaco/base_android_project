package cu.jaco.transito.repositories.retrofit.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import cu.jaco.transito.BuildConfig
import cu.jaco.transito.R
import cu.jaco.transito.repositories.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CertificateUtils(private val certificate: Certificate) {

    companion object {
        const val CERTIFICATE_RAW_PATH = "res/raw/certificate.crt"

        @SuppressLint("LogNotTimber")
        fun providerCertificate(): Certificate {

            val cf = CertificateFactory.getInstance("X.509")

            val classLoader = this::class.java.classLoader

            //magia para que proguard vea que se esta usando el recurso y no lo elimine
            val certId = R.raw.certificate
            Log.d("Proguard", "$certId")

            val cert = classLoader?.getResourceAsStream(CERTIFICATE_RAW_PATH)

            return cert.use {
                cf.generateCertificate(cert)
            }
        }

    }

    /**
     * @param context contexto para cargar el certificado si fuera necesario
     *
     * @return Boolean indicando que es necesario invalidar el certificado actual
     */
    suspend fun checkCertificate(context: Context, url: String): Boolean {
        return if (!isValidCertificate())
            download(context,url)
        else false
    }

    private suspend fun download(context: Context, url: String) = withContext(Dispatchers.IO) {
        val outputFile = getOutputFile(context)
        val downloaded = downloadFile(url, outputFile)
        if (downloaded)
            AppPreferences.certificatePath = outputFile.absolutePath
        downloaded
    }

    private fun isValidCertificate(): Boolean {

//        val certFile = File(AppPreferences.certificatePath)
//
//        if (!certFile.exists())
//            return false

        return try {
            (certificate as X509Certificate).checkValidity()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun autoSignedCertificate(): Certificate {

        val cf = CertificateFactory.getInstance("X.509")

        val classLoader = this::class.java.classLoader
        val cert = classLoader?.getResourceAsStream(CERTIFICATE_RAW_PATH)

        return cert.use {
            cf.generateCertificate(cert)
        }

    }

    private fun getOutputFile(context: Context): File {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val fileName = sdf.format(Date()) + ".pem"
        val mediaStorageDir = context.filesDir
        return File(mediaStorageDir, fileName)
    }

    private fun downloadFile(url: String, outputFile: File): Boolean {

        try {

            val okHttpBuilder = OkHttpClient().newBuilder()
                .connectTimeout(45000L, TimeUnit.MILLISECONDS)
                .readTimeout(45000L, TimeUnit.MILLISECONDS)
                .writeTimeout(45000L, TimeUnit.MILLISECONDS)
                .configSsl(autoSignedCertificate())

            val client = okHttpBuilder.build()
            val request = Request.Builder().url(url)
                .build()
            val response = client
                .newCall(request)
                .execute()

            val body = response.body()
            val contentLength = (body?.contentLength() ?: 0L).coerceAtLeast(0L)
            val fileStream = body?.byteStream()
            val stream = DataInputStream(fileStream)
            val buffer = ByteArray(contentLength.toInt())
            stream.readFully(buffer)
            stream.close()
            if (contentLength != 0L) {
                val fos = DataOutputStream(FileOutputStream(outputFile))
                fos.write(buffer)
                fos.flush()
                fos.close()
            }
            return true
        } catch (e: FileNotFoundException) {
            return false  // swallow a 404
        } catch (e: IOException) {
            return false // swallow a 404
        }

    }

}