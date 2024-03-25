package com.example.wetalk.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesRequest
import com.example.wetalk.data.model.postmodel.VocabulariesDTO
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.NetworkUtil
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class VocabulariesSearchViewModel @Inject constructor(var repository: TalkRepository, @ApplicationContext var context: Context)  :ViewModel(){
    // MutableLiveData for storing l vocabularies resource
    private val _searchVocabularies: MutableLiveData<Resource<GetAllVocabulariesRequest>> =
        MutableLiveData(Resource.Loading())
    val searchVocabularies: LiveData<Resource<GetAllVocabulariesRequest>>
        get() = _searchVocabularies
    private var searchResponse: GetAllVocabulariesRequest? = null
    fun searchVocabulariesData(vocabulariesDTO: VocabulariesDTO) = viewModelScope.launch {
        try {
            // Check for internet connection before making the API call
            _searchVocabularies.value = if (NetworkUtil.hasInternetConnection(context)) {
                // If there is an internet connection, handle the API response
                handleSearchVocabulariesResponse(repository.searchVocabularies(vocabulariesDTO))
            } else {
                // If there is no internet connection, update the StateFlow with an error
                Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            // Handle exceptions and update the StateFlow with an error
            _searchVocabularies.value = Resource.Error(e.toString())
        }
    }
    private fun handleSearchVocabulariesResponse(response: Response<GetAllVocabulariesRequest>): Resource<GetAllVocabulariesRequest> =
        if (response.isSuccessful) {
            // If the API call is successful, return a Success resource with the user information
            Resource.Success(searchResponse ?: response.body()!!)
        } else {
            // If the API call is not successful, return an Error resource with the error message
            Resource.Error((searchResponse ?: response.message()).toString())
        }
}