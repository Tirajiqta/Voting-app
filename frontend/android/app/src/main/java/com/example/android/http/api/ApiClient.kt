package com.example.android.http.api

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

internal object ApiClient {
    val client = OkHttpClient()
    val json = Json { ignoreUnknownKeys = true }

    inline fun <reified T : Any> post(
        url: String,
        body: T,
        headers: Map<String, String> = emptyMap(),
        crossinline onSuccess: (String) -> Unit,
        crossinline onFailure: (Throwable) -> Unit
    ) {

        val jsonBody = json.encodeToString(body)
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        val requestBuilder = Request.Builder()
            .url(url)
            .post(requestBody)

        headers.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = onFailure(e)

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        onSuccess(it.body?.string() ?: "")
                    } else {
                        onFailure(IOException("HTTP ${it.code}: ${it.message}"))
                    }
                }
            }
        })
    }

    inline fun <reified T : Any> get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        crossinline onSuccess: (T) -> Unit,
        crossinline onFailure: (Throwable) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .apply {
                headers.forEach { (k, v) -> addHeader(k, v) }
            }
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = onFailure(e)

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val body = it.body?.string() ?: ""
                        try {
                            val result = json.decodeFromString<T>(body)
                            onSuccess(result)
                        } catch (e: Exception) {
                            onFailure(e)
                        }
                    } else {
                        onFailure(IOException("HTTP ${it.code}: ${it.message}"))
                    }
                }
            }
        })
    }
}
