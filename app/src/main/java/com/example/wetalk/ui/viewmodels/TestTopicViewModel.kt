package com.example.wetalk.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.GetAllQuestion
import com.example.wetalk.data.model.objectmodel.QuestionSize
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TestTopicViewModel @Inject constructor(private val repository: TalkRepository) : ViewModel() {
    // MutableStateFlow for storing questions resource
    private val _questions: MutableStateFlow<Resource<GetAllQuestion>> =
        MutableStateFlow(Resource.Loading())
    val questions: StateFlow<Resource<GetAllQuestion>>
        get() = _questions

    private var hostResponse: GetAllQuestion? = null

    // Function to get all questions by topic ID
    fun getAllQuestionPageByTopicId(questionSize: QuestionSize) = viewModelScope.launch {
        try {
            val response = repository.getAllQuestionPageByTopicID(questionSize)
            _questions.value = handleGetAllQuestion(response)
        } catch (e: Exception) {
            Log.e("GETALLQUESTION_API_ERROR", e.message.toString())
            _questions.value = Resource.Error(e.message.toString())
        }
    }

    // Function to handle response for getting all questions
    private fun handleGetAllQuestion(response: Response<GetAllQuestion>): Resource<GetAllQuestion> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(hostResponse ?: resultResponse)
            }
        } else {

        }
        return Resource.Error((hostResponse ?: response.message()).toString())
    }
}
