package com.example.wetalk.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.User
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val mRepository: TalkRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    private val _registerResponseStateFlow: MutableStateFlow<Resource<HostResponse>> =
        MutableStateFlow(Resource.Loading())
    val registerResponseStateFlow: StateFlow<Resource<HostResponse>>
        get() = _registerResponseStateFlow


    fun generateOtp(user: User) = viewModelScope.launch {
       mRepository.generateOtp(user)
    }


}