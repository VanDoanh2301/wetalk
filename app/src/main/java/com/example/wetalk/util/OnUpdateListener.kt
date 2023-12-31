package com.example.wetalk.util

interface OnUpdateListener<T> {
    fun updateData(result:T, index:Int)
}