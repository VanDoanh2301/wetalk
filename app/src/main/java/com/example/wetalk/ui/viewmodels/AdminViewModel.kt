package com.example.wetalk.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.postmodel.TopicPost
import com.example.wetalk.repository.TalkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AdminViewModel @Inject constructor(private val repository: TalkRepository) : ViewModel() {

      fun addTopic(topicPost: TopicPost) = viewModelScope.launch {
          val response = repository.addTopic(topicPost)
          if (response.isSuccessful) {
              Log.d(TAG, "Add Success")
          } else {

          }
      }
    fun deleteTopic(id: Int) = viewModelScope.launch {
        repository.deleteTopic(id)
    }

    companion object {
        const val TAG = "ADMIN_VIEWMODEL"
    }
}