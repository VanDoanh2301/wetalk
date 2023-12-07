package com.example.wetalk.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.data.local.VideoBody
import com.example.wetalk.data.local.VideoBodyItem
import com.example.wetalk.data.local.VideoLocal
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ViewModelScoped
class TalkVocabularyViewModel : ViewModel() {

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

}

