package com.example.wetalk.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.data.local.VideoBody
import com.example.wetalk.data.local.VideoBodyItem
import com.example.wetalk.data.local.VideoLocal
import com.example.wetalk.data.model.objectmodel.Message
import com.example.wetalk.data.model.postmodel.MediaValidatePost
import com.example.wetalk.data.model.responsemodel.ValidationResponse
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.LogUtils
import com.example.wetalk.util.NetworkUtil
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

// ViewModel class for handling vocabulary upload
@HiltViewModel
class VideoUpViewModel @Inject constructor(
    private val mRepository: TalkRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // LiveData for observing upload result
    private val _uploadResult: MutableLiveData<Resource<String>> =
        MutableLiveData(Resource.Loading())
    val uploadResult: LiveData<Resource<String>>
        get() = _uploadResult

    private val _validMedia: MutableLiveData<Resource<ValidationResponse>> =
        MutableLiveData(Resource.Loading())
    val validMedia: LiveData<Resource<ValidationResponse>> get() = _validMedia

    private val validationResponse: ValidationResponse? = null

    // StateFlow for holding video local data
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
    val videoLocal: StateFlow<VideoLocal> = _videoLocal

    // StateFlow for holding image items
    private val _talkImageItems = MutableStateFlow(ArrayList<StorageImageItem>())
    val talkImageItems: StateFlow<ArrayList<StorageImageItem>> = _talkImageItems

    // Function to set video local data
    fun setVideoLocal(videoLocal: VideoLocal) {
        _videoLocal.value = videoLocal
    }

    // Function to add image items
    fun addImageItems(items: ArrayList<StorageImageItem>) {
        _talkImageItems.value = items
    }

    // Function to initiate video upload
    fun uploadVideo(file: MultipartBody.Part) {
        viewModelScope.launch {
            updateVideo(file)
        }
    }

    // Function to update video data
    private suspend fun updateVideo(file: MultipartBody.Part) {
        _uploadResult.value = Resource.Loading()
        try {
            if (NetworkUtil.hasInternetConnection(context)) {
                val response = mRepository.uploadVideo(file)
                _uploadResult.value = handleUploadResponse(response)
            } else {
                _uploadResult.value =
                    Resource.Error("Mất Kết Nối Internet") // Error message in Vietnamese
            }
        } catch (e: Exception) {
            _uploadResult.value = Resource.Error("${e.message.toString()}")
        }
    }

    // Function to handle upload response
    private fun handleUploadResponse(response: Response<String>): Resource<String> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else {
            // Handling different HTTP error codes
            val res = when (response.code()) {
                401 -> "Error"
                400 -> "Invalid request body"
                500 -> "Internal server error"
                else -> response.message()
            }
            return Resource.Error(res)
        }
        return Resource.Error(response.message())
    }

    fun getValidMedia(mediaValidatePost: MediaValidatePost) = viewModelScope.launch {
        try {
            val response = mRepository.validationMedia(mediaValidatePost)
            _validMedia.value = handleValidMedia(response)
        } catch (e: Exception) {
            _validMedia.value = Resource.Error(e.message.toString())
        }
    }

    private fun handleValidMedia(response: Response<ValidationResponse>): Resource<ValidationResponse>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(validationResponse ?: it)
            }
        } else {
        }
        return Resource.Error((validationResponse ?: response.message()).toString())
    }
}
