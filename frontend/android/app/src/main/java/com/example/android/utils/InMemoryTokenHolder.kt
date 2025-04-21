package com.example.android.utils

import android.util.Log

object InMemoryTokenHolder {

    private var currentToken: String? = null
    private const val TAG = "InMemoryTokenHolder"

    /**
     * Stores the token in memory. Replaces any existing token.
     *
     * @param token The token string to store, or null to clear.
     */
    fun saveToken(token: String?) {
        currentToken = token
        if (token != null) {
            Log.d(TAG, "Token updated in memory.") // Log without showing token
        } else {
            Log.d(TAG, "Token cleared from memory.")
        }
    }

    /**
     * Retrieves the token currently held in memory.
     *
     * @return The token string, or null if none is stored.
     */
    fun getToken(): String? {
        return currentToken
    }

    /**
     * Clears the token currently held in memory.
     */
    fun clearToken() {
        saveToken(null) // Simply save null to clear it
    }

    /**
     * Checks if a token is currently held in memory.
     *
     * @return True if a non-null token exists, false otherwise.
     */
    fun hasToken(): Boolean {
        return currentToken != null
    }
}