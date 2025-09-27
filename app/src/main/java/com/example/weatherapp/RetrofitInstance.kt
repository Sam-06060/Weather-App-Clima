package com.example.weatherapp

// --- NEW IMPORTS ---
import com.example.weatherapp.BuildConfig // Accesses your secure key
import okhttp3.OkHttpClient // The engine for making network calls
import okhttp3.logging.HttpLoggingInterceptor // Optional: For debugging network calls
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    // 1. Get the API key securely from your Gradle file
    private const val API_KEY = BuildConfig.API_KEY

    // 2. Create an OkHttpClient with an Interceptor
    private val client = OkHttpClient.Builder().apply {
        // Create an interceptor to add the API key to all requests
        addInterceptor { chain ->
            val originalRequest = chain.request()
            val originalHttpUrl = originalRequest.url

            // Build the new URL with the "appid" query parameter
            val newUrl = originalHttpUrl.newBuilder()
                .addQueryParameter("appid", API_KEY)
                .build()

            // Build the new request with the new URL
            val newRequest = originalRequest.newBuilder().url(newUrl).build()

            // Proceed with the new request
            chain.proceed(newRequest)
        }

        // Optional: Add a logging interceptor to see request/response details in Logcat
        // This is very useful for debugging!
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        addInterceptor(logging)

    }.build()


    // 3. Build Retrofit using the custom OkHttpClient
    val api: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // <-- Attach the custom client here
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}