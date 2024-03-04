package com.example.wetalk.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wetalk.repository.TalkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TabFriendViewModel @Inject constructor(private val response: TalkRepository) : ViewModel(){

}