package com.example.wetalk.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PracticeQuest(
    var id: Int,
    var question: Question,
    var answerUser: String,
    var learnedAudio: Boolean,
    var zIndex: Int
) : Parcelable {}