package com.github.boritjjaroo.gflib.data

import com.google.gson.JsonObject

class UserInfo {
    var userId = ""
    var lastLoginTime: Long = 0

    fun initSession() {
        userId = ""
        lastLoginTime = 0
    }

    // parse from the UserInfo packet's root JsonObject
    fun parseJson(json: JsonObject) {
        val jsonUserInfo = json.getAsJsonObject("user_info")
        userId = jsonUserInfo?.get("user_id")?.asString ?: ""
        lastLoginTime = jsonUserInfo?.get("last_login_time")?.asLong ?: 0
    }
}