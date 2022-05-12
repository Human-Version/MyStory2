package com.dicoding.android.mystory2.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.android.mystory2.data.database.StoryRepository
import com.dicoding.android.mystory2.data.response.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainVIewModel @Inject constructor(repository: StoryRepository) : ViewModel(){

    val isLoading: LiveData<Boolean> = repository.isLoading

    val story: LiveData<PagingData<Story>> = repository.getStory().cachedIn(viewModelScope)

}