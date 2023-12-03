package com.example.wetalk.util

interface Task<T> {
    fun callback(result: T)
}
