package com.example.wetalk.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.data.local.VideoBody
import com.example.wetalk.data.local.VideoBodyItem
import com.example.wetalk.data.local.VideoLocal
import com.example.wetalk.data.model.postmodel.LoginPost
import com.example.wetalk.data.model.responsemodel.Data
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.LogUtils
import com.example.wetalk.util.NetworkUtil
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TalkVocabularyViewModel @Inject constructor(
    private val mRepository: TalkRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uploadResult: MutableStateFlow<Resource<String>> =
        MutableStateFlow(Resource.Loading())
    val uploadResult: StateFlow<Resource<String>>
        get() = _uploadResult
    private val _videoLocal = MutableStateFlow(
        VideoLocal(
            -1,
            System.currentTimeMillis(),
            "",
            VideoBody(ArrayList<VideoBodyItem>()),
            "",
            1
        )
    )
    private var res: String? = null
    val videoLocal: StateFlow<VideoLocal> = _videoLocal
    private val _talkImageItems = MutableStateFlow(ArrayList<StorageImageItem>())
    val talkImageItems: StateFlow<ArrayList<StorageImageItem>> = _talkImageItems

    fun setVideoLocal(videoLocal: VideoLocal) {
        viewModelScope.launch {
            _videoLocal.emit(videoLocal)
        }
    }

    fun addImageItems(items: ArrayList<StorageImageItem>) {
        viewModelScope.launch {
            _talkImageItems.emit(items)
        }
    }

    fun uploadVideo(
        file: MultipartBody.Part)
    {
        viewModelScope.launch {
            updateVideo(file)
        }

    }

    private suspend fun updateVideo(
        file: MultipartBody.Part
    ) {

        _uploadResult.value = Resource.Loading()
        try {
            if (NetworkUtil.hasInternetConnection(context)) {
                val response = mRepository.uploadVideo(file)
                _uploadResult.value = handleUploadResponse(response)
            } else {
                _uploadResult.value = Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            LogUtils.d("LOGIN_API_ERROR: ${e.message}")
            _uploadResult.value = Resource.Error("${e.message.toString()}")
        }
    }

    private fun handleUploadResponse(response: Response<String>): Resource<String> {
        if (response.isSuccessful) {
            LogUtils.d("LOGIN_RETROFIT_SUCCESS: OK")
            response.body()?.let { resultResponse ->
                return Resource.Success(res ?: resultResponse)
            }
        } else {
            var res = response.body().toString()
            if (response.code() == 401) res = "Error"
            else if (response.code() == 400) res = "Invalid request body"
            else if (response.code() == 500) res = "Internal server error"
            return Resource.Error(res)

            LogUtils.d("LOGIN_RETROFIT_ERROR: $response")
        }
        return Resource.Error((res ?: response.message()).toString())
    }
}

