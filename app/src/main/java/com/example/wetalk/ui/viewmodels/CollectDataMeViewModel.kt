package com.example.wetalk.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.GetAllCollectData
import com.example.wetalk.data.model.postmodel.DataPostSearch
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.NetworkUtil
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CollectDataMeViewModel @Inject constructor(var repository: TalkRepository, @ApplicationContext var context: Context)  :
    ViewModel(){
    // MutableLiveData for storing l collectDatas resource
    private val _collectDatas: MutableLiveData<Resource<GetAllCollectData>> =
        MutableLiveData(Resource.Loading())
    val collectDatas: LiveData<Resource<GetAllCollectData>>
        get() = _collectDatas
    private var vocabulariesResponse:GetAllCollectData? = null
    fun getCollectDataMe() = viewModelScope.launch {
        try {
            // Check for internet connection before making the API call
            _collectDatas.value = if (NetworkUtil.hasInternetConnection(context)) {
                // If there is an internet connection, handle the API response
                handleSearchVocabulariesResponse(repository.getCollectDataMe())
            } else {
                // If there is no internet connection, update the StateFlow with an error
                Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            // Handle exceptions and update the StateFlow with an error
            _collectDatas.value = Resource.Error(e.toString())
        }
    }
    private fun handleSearchVocabulariesResponse(response: Response<GetAllCollectData>): Resource<GetAllCollectData> =
        if (response.isSuccessful) {
            // If the API call is successful, return a Success resource with the user information
            Resource.Success(vocabulariesResponse ?: response.body()!!)
        } else {
            // If the API call is not successful, return an Error resource with the error message
            Resource.Error((vocabulariesResponse?: response.message()).toString())
        }


    fun getCollectDataRejectMe() = liveData {
        emit(repository.getAllRejectListMe())
    }
    fun getCollectDataApprovedMe() = liveData {
        emit(repository.getAllApprovedListMe())
    }
    fun searchCollectData(postSearch: DataPostSearch) = liveData{
        emit(repository.searchDataCollectionMe(postSearch))
    }

}