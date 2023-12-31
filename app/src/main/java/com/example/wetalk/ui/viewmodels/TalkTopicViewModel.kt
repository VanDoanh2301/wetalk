package com.example.wetalk.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.GetAllTopic
import com.example.wetalk.data.model.postmodel.LoginPost
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.data.model.responsemodel.LoginResponse
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TalkTopicViewModel @Inject constructor(private val repository: TalkRepository) : ViewModel() {
    private val _topics: MutableStateFlow<Resource<GetAllTopic>> =
        MutableStateFlow(Resource.Loading())
    val topic: StateFlow<Resource<GetAllTopic>>
        get() = _topics

    private var hostResponse: GetAllTopic? = null

    fun getAllTopic() = viewModelScope.launch {
        try {
            val response = repository.getAllTopic()
            _topics.value = handleGetAllTopic(response)
        } catch (e: Exception) {
            Log.e("GETALLTOPIC_API_ERROR", e.message.toString())
            _topics.value = Resource.Error(e.message.toString())
        }
    }

    private fun handleGetAllTopic(response: Response<GetAllTopic>): Resource<GetAllTopic> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(hostResponse ?: resultResponse)
            }
        } else {
            Log.e("GETALLTOPIC_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((hostResponse ?: response.message()).toString())
    }


}