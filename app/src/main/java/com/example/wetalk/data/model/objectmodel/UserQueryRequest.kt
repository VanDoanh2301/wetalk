package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserQueryRequest(
    val page: Int,
    val size: Int,
    val text: String,
    val ascending: Boolean,
    val orderBy: String
) : Parcelable