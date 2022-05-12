package com.dicoding.android.mystory2.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.android.mystory2.data.database.StoryRepository
import com.dicoding.android.mystory2.data.response.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: StoryRepository) : ViewModel() {

    val userLogin: LiveData<LoginResult> = repository.userLogin

    val toastMessage: LiveData<String> = repository.toastMessage

    val isLoading: LiveData<Boolean> = repository.isLoading

    fun loginUser(email: String, password: String) = repository.loginUser(email, password)

}