package com.dicoding.android.mystory2.data.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.dicoding.android.mystory2.data.UserPreference
import com.dicoding.android.mystory2.data.remote.ApiService
import com.dicoding.android.mystory2.data.remote.StoryRemoteMediator
import com.dicoding.android.mystory2.data.response.*
import com.dicoding.android.mystory2.util.RETROFIT_TAG
import com.dicoding.android.mystory2.util.wrapEspressoIdlingResource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val preference: UserPreference
) {

    private val _userLogin = MutableLiveData<LoginResult>()
    val userLogin: LiveData<LoginResult> = _userLogin

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _listStory = MutableLiveData<List<Story>>()
    val listStory: LiveData<List<Story>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    @OptIn(ExperimentalPagingApi::class)
    fun getStory(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase,apiService, preference),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }


    fun getStoryWithLocation(token: String) {
        wrapEspressoIdlingResource {
            apiService.getListStoryWithLocation(token, 1)
                .enqueue(object : Callback<StoriesResponse> {
                    override fun onResponse(
                        call: Call<StoriesResponse>,
                        response: Response<StoriesResponse>
                    ) {
                        if (response.isSuccessful) {
                            _listStory.postValue(response.body()?.listStory)
                        }
                    }

                    override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                        Log.d(RETROFIT_TAG, t.message.toString())
                    }

                })
        }

    }

    fun loginUser(email: String, password: String) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            apiService.loginUser(email, password)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            _toastMessage.value = response.body()?.message
                            _userLogin.value = response.body()?.loginResult
                            Log.d(RETROFIT_TAG, response.body()?.message.toString())
                            Log.d(RETROFIT_TAG, response.body()?.loginResult?.token.toString())
                            Log.d(RETROFIT_TAG, response.body()?.loginResult?.name ?: "name")
                            Log.d(RETROFIT_TAG, response.body()?.loginResult?.userId ?: "userId")
                        }
                        if (!response.isSuccessful) {
                            _toastMessage.value = response.message()
                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        _toastMessage.value = t.message
                        _isLoading.value = false
                        Log.d(RETROFIT_TAG, t.message.toString())
                    }

                })
        }

    }


    fun registerUser(name: String, email: String,password: String) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            apiService.registerUser(name, email, password)
                .enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            Log.d(RETROFIT_TAG, response.body()?.message.toString())
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        _isLoading.value = false
                        Log.d(RETROFIT_TAG, t.message.toString())
                    }

                })
        }

    }

    fun addStory(token: String, photo: File, description: String){
        wrapEspressoIdlingResource {
            val descriptionText = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                photo.name,
                requestImageFile
            )
            val service = apiService.uploadImage(token, imageMultipart, descriptionText)
            service.enqueue(object : Callback<AddResponse> {
                override fun onResponse(call: Call<AddResponse>, response: Response<AddResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            _toastMessage.value = responseBody.message
                        } else {
                            _toastMessage.value =  response.message()
                        }
                    }
                }

                override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                    _toastMessage.value = "instance retrofit fail"
                }

            })
        }
    }


    fun postStoryWithLOcation(token: String, imageMultipart: MultipartBody.Part, description: RequestBody, latitude: Float, longitude: Float) {
        wrapEspressoIdlingResource {
            apiService.uploadImageWithLocation(token, imageMultipart, description, latitude, longitude)
                .enqueue(object : Callback<AddResponse> {
                    override fun onResponse(call: Call<AddResponse>, response: Response<AddResponse>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error) {
                                _toastMessage.value = responseBody.message
                            } else {
                                _toastMessage.value =  response.message()
                            }
                        }
                    }

                    override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                        _toastMessage.value = "instance retrofit fail"
                    }

                })
        }

    }

}