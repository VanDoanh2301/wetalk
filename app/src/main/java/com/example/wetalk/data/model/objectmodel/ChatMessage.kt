package com.example.wetalk.data.model.objectmodel

data class ChatMessage(var id: Int, var  content:String, var messageType:String = "TEXT", var mediaLocation:String ?= null)
