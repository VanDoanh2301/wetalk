package com.example.wetalk.data.local


data  class VideoLocal(
     var id: Long,
     var time: Long,
     var title: String,
     var videoBodies: VideoBody,
     var videoTag: String,
     var isActive: Int
) {
     companion object {
          fun newVideo(): VideoLocal {
               return VideoLocal(
                    -1, System.currentTimeMillis(), "",
                    VideoBody(ArrayList<VideoBodyItem>()), "", 1
               )
          }
     }

}