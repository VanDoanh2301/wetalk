package com.example.wetalk.data.model.postmodel

import com.google.gson.annotations.SerializedName

data  class LoginDTO(@SerializedName("email")
                      val email: String,
                     @SerializedName("password")
                      val password: String) {
}