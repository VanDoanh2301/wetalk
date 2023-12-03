package com.example.wetalk.data.local

data  class VideoLocal (
     var id: Long,
     var time: Long,
     var title: String,
     var videoBodies: VideoBody,
     var diaryTag: String,
     var isActive:Boolean)