package com.dicoding.android.mystory2.ui.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.android.mystory2.data.database.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(private val repository: StoryRepository): ViewModel() {

    val toastMessage: LiveData<String> = repository.toastMessage

    fun addStory(token: String, imageMultipart: File, description: String) {
        repository.addStory(token, imageMultipart, description)
    }

}