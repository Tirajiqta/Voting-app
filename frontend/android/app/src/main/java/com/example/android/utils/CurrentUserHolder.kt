package com.example.android.utils

import com.example.android.dto.response.UserProfileDetailsDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CurrentUserHolder {

    // Use StateFlow for observing changes reactively (optional but good practice)
    private val _userProfileDetails = MutableStateFlow<UserProfileDetailsDTO?>(null)
    val userProfileDetails: StateFlow<UserProfileDetailsDTO?> = _userProfileDetails.asStateFlow()

    /**
     * Updates the stored user profile details.
     * Typically called after successful login and profile fetch.
     * @param details The fetched user profile details, or null on error/logout.
     */
    fun updateProfile(details: UserProfileDetailsDTO?) {
        _userProfileDetails.value = details
        // Log if needed:
        // if (details != null) {
        //     Log.i("CurrentUserHolder", "User profile updated: ${details.user.name}")
        // } else {
        //     Log.i("CurrentUserHolder", "User profile cleared.")
        // }
    }

    /**
     * Clears the stored user profile details.
     * Typically called on logout or when fetching fails.
     */
    fun clear() {
        _userProfileDetails.value = null
    }

    /**
     * Convenience function to get the current value directly (non-reactive).
     * Use the StateFlow 'userProfileDetails' for Compose UI observation.
     */
    fun getCurrentProfile(): UserProfileDetailsDTO? {
        return _userProfileDetails.value
    }

    /**
     * Checks if a user profile is currently loaded.
     */
    fun isLoggedIn(): Boolean {
        return _userProfileDetails.value != null
    }
}