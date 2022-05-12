package com.dicoding.android.mystory2.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.android.mystory2.data.UserPreference
import com.dicoding.android.mystory2.data.response.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataStoreViewModel @Inject constructor(private val preference: UserPreference): ViewModel() {

    fun getSession(): LiveData<UserSession> {
        return preference.getUser().asLiveData()
    }

    fun setSession(user: UserSession) {
        viewModelScope.launch {
            preference.setUser(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            preference.logout()
        }
    }

}