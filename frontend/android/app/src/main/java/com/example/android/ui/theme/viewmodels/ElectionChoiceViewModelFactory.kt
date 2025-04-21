package com.example.android.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.repository.ElectionsRepository

class ElectionChoiceViewModelFactory(
    private val electionRepository: ElectionsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionChoiceViewModel::class.java)) {
            return ElectionChoiceViewModel(electionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}