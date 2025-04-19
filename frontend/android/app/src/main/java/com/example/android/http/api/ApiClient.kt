package com.example.android.api

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

internal object ApiClient {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    inline fun <reified Req : Any, reified Res : Any> post(
        url: String,
        body: Req,
        headers: Map<String, String> = emptyMap(),
        crossinline onSuccess: (Res) -> Unit,
        crossinline onFailure: (Throwable) -> Unit
    ) {
        val jsonBody = json.encodeToString(body)
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        val requestBuilder = Request.Builder().url(url).post(requestBody)
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = onFailure(e)

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val bodyString = it.body?.string() ?: ""
                        try {
                            val result = json.decodeFromString<Res>(bodyString)
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

    inline fun <reified T : Any> get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        crossinline onSuccess: (T) -> Unit,
        crossinline onFailure: (Throwable) -> Unit
    ) {
        val requestBuilder = Request.Builder().url(url).get()
        headers.forEach { (k, v) -> requestBuilder.addHeader(k, v) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = onFailure(e)

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val bodyString = it.body?.string() ?: ""
                        try {
                            val result = json.decodeFromString<T>(bodyString)
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
