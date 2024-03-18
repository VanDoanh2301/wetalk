package com.example.wetalk.data.model.postmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VocabulariesDTO(
    val page: Int,
    val size: Int,
    val text: String,
    val ascending: Boolean,
    val orderBy: String,
    val  topicId: Int
) : Parcelable