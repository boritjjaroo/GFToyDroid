package com.github.boritjjaroo.gflib.data

import com.google.gson.JsonObject

class SkinData() {
    // skin id
    var id: Int = 0
    // skin group id
    var groupId: Int = 0
    // skin name
    var name: String = ""
    // gun id that skin is used for
    var gunId: Int = 0

    constructor(id: Int, groupId: Int, name: String, gunId: Int) : this() {
        this.id = id
        this.groupId = groupId
        this.name = name
        this.gunId = gunId
    }

    constructor(json: JsonObject) : this() {
        this.id = json.get("id").asInt
        this.groupId = json.get("group").asInt
        this.name = json.get("name").asString
        this.gunId = json.get("gun").asInt
    }

    // { "skin_id": "201", "user_id": "1870807", "is_read": "0" }
    // is_read : If a user has read the skin's story or not
    fun generateJsonSkinWithUserInfo() : JsonObject {
        val json = JsonObject()
        json.addProperty("skin_id", this.id.toString())
        json.addProperty("user_id", GfData.userInfo.userId)
        json.addProperty("is_read", "0")
        return json
    }
}
