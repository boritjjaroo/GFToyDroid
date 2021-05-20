package com.github.boritjjaroo.gflib.data

import com.google.gson.JsonObject

class UserInfo {
    var userId = ""

    // parse from the UserInfo packet's root JsonObject
    fun parseJson(json: JsonObject) {
        val jsonUserInfo = json.get("user_info").asJsonObject
        userId = jsonUserInfo.get("user_id").asString
    }
}