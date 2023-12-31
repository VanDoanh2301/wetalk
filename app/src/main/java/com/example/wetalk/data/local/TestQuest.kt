package com.example.wetalk.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TestQuest(
    var question: QuestionType,
    var answer: String
) : Parcelable {}