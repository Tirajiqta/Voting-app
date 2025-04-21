package com.example.android.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.android.api.VotingApi
import com.example.android.dto.request.LoginRequest
import com.example.android.dto.response.LoginResponse
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class GeneralUtil {

    fun generateAesKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256) // 256-bit AES
        return keyGen.generateKey()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptVoteAES(voteData: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = ByteArray(16).apply {
            SecureRandom().nextBytes(this)
        }

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        val encryptedBytes = cipher.doFinal(voteData.toByteArray(Charsets.UTF_8))

        val encryptedWithIv = iv + encryptedBytes
        return Base64.getEncoder().encodeToString(encryptedWithIv)
    }
}