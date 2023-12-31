package com.example.wetalk.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PracticeQuest(
    var id: Int,
    var questionType: QuestionType,
    var answerUser: String,
    var learnedVideo: Boolean,
    var zIndex: Int
) : Parcelable {}