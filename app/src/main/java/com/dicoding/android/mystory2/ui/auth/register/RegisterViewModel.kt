package com.dicoding.android.mystory2.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.android.mystory2.data.database.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: StoryRepository) : ViewModel() {

    val isLoading: LiveData<Boolean> = repository.isLoading

    fun registerUser(name: String, email: String,password: String) {
        repository.registerUser(name, email, password)
    }

}