package com.dicoding.android.mystory2.data.database

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.android.mystory2.data.UserPreference
import com.dicoding.android.mystory2.data.remote.ApiService
import com.dicoding.android.mystory2.data.response.Story
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StoryPagingSource @Inject constructor(private val userPreference: UserPreference, private val apiService: ApiService): PagingSource<Int, Story>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val token: String = userPreference.getUser().first().token
            val responseData = apiService.getListStory("Bearer $token", page, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}