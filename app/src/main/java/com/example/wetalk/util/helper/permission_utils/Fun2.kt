package com.example.wetalk.util.helper.permission_utils


abstract class Func2 {
    abstract fun call(
        requestCode: Int,
        permissions: Array<String?>?,
        grantResults: IntArray?
    )
}