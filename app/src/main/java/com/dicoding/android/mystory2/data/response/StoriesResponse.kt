package com.dicoding.android.mystory2.data.response

data class StoriesResponse(
    val error: Boolean,
    val listStory: List<Story>,
    val message: String
)