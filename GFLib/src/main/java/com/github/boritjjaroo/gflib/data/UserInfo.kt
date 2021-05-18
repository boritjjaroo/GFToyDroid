package com.github.boritjjaroo.gflib.data

import com.google.gson.JsonObject

class UserInfo {
    var id = ""

    // parse from the UserInfo packet's root JsonObject
    fun parseJson(json: JsonObject) {
        val jsonUserInfo = json.get("user_info").asJsonObject
        id = jsonUserInfo.get("id").asString
    }
}