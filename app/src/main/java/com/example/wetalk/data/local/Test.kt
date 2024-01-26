package com.example.wetalk.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Test(
    var total:Int,
    var correct:Int,
    var check:Int,
    var completed:Int
) : Parcelable {}