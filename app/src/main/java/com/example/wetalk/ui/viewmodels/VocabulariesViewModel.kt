package com.example.wetalk.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.GetAllTopic
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesByIdRequest
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class VocabulariesViewModel @Inject constructor(var repository: TalkRepository)  :ViewModel(){
    // MutableStateFlow for storing l vocabularies resource
    private val _vocabularies: MutableStateFlow<Resource<GetAllVocabulariesByIdRequest>> =
        MutableStateFlow(Resource.Loading())
    val vocabularies: StateFlow<Resource<GetAllVocabulariesByIdRequest>>
        get() = _vocabularies

    private var hostResponse: GetAllVocabulariesByIdRequest? = null

    // Function to get all topics
    fun getAllVocabulariesByTopicId(topicId :Int) = viewModelScope.launch {
        try {
            val response = repository.getVocabulariesById(topicId)
            _vocabularies.value = handleGetVocabulariesById(response)
        } catch (e: Exception) {
            Log.e("GETALLTOPIC_API_ERROR", e.message.toString())
            _vocabularies.value = Resource.Error(e.message.toString())
        }
    }
    // Function to handle response for getting all vocabularies
    private fun handleGetVocabulariesById(response: Response<GetAllVocabulariesByIdRequest>): Resource<GetAllVocabulariesByIdRequest> {
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