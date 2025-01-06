package com.example.containertracker.utils

import android.content.Context
import android.content.Intent
import com.example.containertracker.BuildConfig
import com.example.containertracker.ui.login.LoginActivity
import com.example.containertracker.utils.constants.TimeoutConstant.OKHTTP_CONNECTION_TIMEOUT
import com.example.containertracker.utils.constants.TimeoutConstant.OKHTTP_READ_TIMEOUT
import com.example.containertracker.utils.constants.TimeoutConstant.OKHTTP_WRITE_TIMEOUT
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object NetworkUtil {
    const val SERVER_HOST = "http://10.19.51.34/"
    const val BASE_URL = "${SERVER_HOST}api/"
    const val REQUEST_NOT_FOUND = "not_found"
    fun buildClient(applicationContext: Context): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

//        val requestInterceptor = Interceptor { chain ->
//            val original = chain.request()
//            val request = original.newBuilder()
//                .header(APP_VERSION, BuildConfig.VERSION_NAME.split("-").getOrNull(0).orDash())
//                .header(DEVICE_ID, DeviceUtil(applicationContext).getDeviceId())
//                .method(original.method, original.body)
//                .build()
//            chain.proceed(request)
//        }
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(OKHTTP_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        builder.readTimeout(OKHTTP_READ_TIMEOUT, TimeUnit.SECONDS)
        builder.writeTimeout(OKHTTP_WRITE_TIMEOUT, TimeUnit.SECONDS)
        builder.addInterceptor(httpLoggingInterceptor)
//        builder.addInterceptor(getChucker(applicationContext))
        builder.addInterceptor{ chain: Interceptor.Chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            if(!response.isSuccessful){
                if(response.code == 401){
                    onTokenExpired(applicationContext)
                }
            }
            response
        }
        //Enable Stetho Interceptor only at Debugging Mode
//        if (BuildConfig.DEBUG) builder.addNetworkInterceptor(StethoInterceptor())
//        builder.addInterceptor(ChuckInterceptor(applicationContext))
//        builder.addNetworkInterceptor(requestInterceptor)
//        builder.connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS))
        return builder.build()
    }

    private fun onTokenExpired(context: Context) {
//        if (UserUtil.getToken().isNotBlank()) {
//            PreferenceUtil.truncateStorage()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
//        }
    }

//    private fun getChucker(context: Context): Interceptor {
//        // Create the Collector
//        val chuckerCollector = ChuckerCollector(
//            context = context,
//            // Toggles visibility of the notification
//            showNotification = true,
//            // Allows to customize the retention period of collected data
//            retentionPeriod = RetentionManager.Period.ONE_HOUR
//        )
//
//        // Create the Interceptor
//        return ChuckerInterceptor.Builder(context)
//            // The previously created Collector
//            .collector(chuckerCollector)
//            // The max body content length in bytes, after this responses will be truncated.
//            .maxContentLength(250_000L)
//            // List of headers to replace with ** in the Chucker UI
//            .redactHeaders("Auth-Token", "Bearer")
//            // Read the whole response body even when the client does not consume the response completely.
//            // This is useful in case of parsing errors or when the response body
//            // is closed before being read like in Retrofit with Void and Unit types.
//            .alwaysReadResponseBody(true)
//            .build()
//    }

    inline fun <reified T> buildService(baseUrl: String, okHttpClient: OkHttpClient): T {
        val gson = GsonBuilder()
            .enableComplexMapKeySerialization()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .setVersion(1.0)
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .build()

        return retrofit.create(T::class.java)
    }
}