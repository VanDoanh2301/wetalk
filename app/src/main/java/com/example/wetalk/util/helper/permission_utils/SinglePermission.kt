package com.example.wetalk.util.helper.permission_utils

/**
 * Created by kayvan on 10/27/15.
 */
class SinglePermission {
    var permissionName: String
    var isRationalNeeded = false
    private var mReason: String? = null

    constructor(permissionName: String) {
        this.permissionName = permissionName
    }

    constructor(permissionName: String, reason: String?) {
        this.permissionName = permissionName
        mReason = reason
    }

    var reason: String?
        get() = if (mReason == null) "" else mReason
        set(reason) {
            mReason = reason
        }
}