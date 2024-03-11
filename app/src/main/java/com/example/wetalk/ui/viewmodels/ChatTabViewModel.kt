package com.example.wetalk.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.GetAllListConversations
import com.example.wetalk.data.model.objectmodel.GetAllUserFriendRequest
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ChatTabViewModel @Inject constructor(private val repository: TalkRepository) : ViewModel() {

    private var _conversions: MutableLiveData<Resource<GetAllListConversations>> =
        MutableLiveData(Resource.Loading())
    val conversions: LiveData<Resource<GetAllListConversations>> get() = _conversions
    private var conversionsResponse: GetAllListConversations? = null

    fun getAllListConversations() = viewModelScope.launch {
        try {
            val response = repository.getAllConversations()
            _conversions.value = handleGetAllListConversations(response)
        } catch (e: Exception) {
            _conversions.value = Resource.Error(e.message.toString())
        }
    }

    private fun handleGetAllListConversations(response: Response<GetAllListConversations>): Resource<GetAllListConversations>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(conversionsResponse ?: it)
            }
        } else {
        }
        return Resource.Error((conversions ?: response.message()).toString())
    }
}