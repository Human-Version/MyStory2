package com.dicoding.android.mystory2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dicoding.android.mystory2.data.local.RemoteKeysDao
import com.dicoding.android.mystory2.data.local.StoryDao
import com.dicoding.android.mystory2.data.remote.RemoteKeys
import com.dicoding.android.mystory2.data.response.Story

@Database(
    entities = [Story::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase(){

    abstract fun storyDao(): StoryDao

    abstract fun remoteKeysDao(): RemoteKeysDao
}