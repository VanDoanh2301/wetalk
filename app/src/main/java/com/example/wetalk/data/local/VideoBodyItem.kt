package com.example.wetalk.data.local

data class VideoBodyItem(var storageImage: StorageImage) {

    companion object {
        fun newImageItem(paths: ArrayList<StorageImageItem>): VideoBodyItem {
            return VideoBodyItem(StorageImage(paths))
        }
    }
}
