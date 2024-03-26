package com.example.wetalk.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.wetalk.data.model.objectmodel.Question
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.objectmodel.VocabularyRequest
import com.example.wetalk.data.model.postmodel.QuestionPost
import com.example.wetalk.repository.TalkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(private val repository: TalkRepository) : ViewModel() {
    //Topic---------------------------------------------------------------------------------------
    fun addTopic(topicPost: TopicRequest) = liveData {
        emit(repository.addTopic(topicPost))
    }
    fun updateTopic(topicRequest: TopicRequest) = liveData {
        emit(repository.updateTopic(topicRequest))
    }
    fun deleteTopic(id: Int) = liveData {
        emit(repository.deleteTopic(id))
    }
    //Vocabulary-----------------------------------------------------------------------------------
    fun addVocabulary(topicPost: TopicRequest) = liveData {
        emit(repository.addVocabulary(topicPost))
    }
    fun updateVocabulary(topicRequest: VocabularyRequest) = liveData {
        emit(repository.updateVocabulary(topicRequest))
    }
    fun deleteVocabulary(id: Int) = liveData {
        emit(repository.deleteVocabulary(id))
    }
    //Question------------------------------------------------------------------------------------
    fun addQuestion(question: QuestionPost) = liveData {
        emit(repository.createQuestion(question))
    }
    fun updateQuestion(question: Question) = liveData {
        emit(repository.updateQuestion(question))
    }
    fun deleteQuestion(id: Int) = liveData {
        emit(repository.deleteQuestion(id))
    }
    //Collect data ---------------------------------------------------------------------------------
    fun getAllDataPending() = liveData {
        emit(repository.getAllDataPending())
    }
    fun getAllDataMe() = liveData {
        emit(repository.getAllDataMe())
    }
    fun approvedCollectData(id: Int) = liveData {
        emit(repository.approveDataById(id))
    }
    fun deleteCollectData(id: Int) = liveData {
        emit(repository.deleteDataById(id))
    }
    companion object {
        const val TAG = "ADMIN_VIEWMODEL"
    }

}