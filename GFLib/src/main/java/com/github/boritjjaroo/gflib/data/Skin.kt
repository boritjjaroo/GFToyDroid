package com.github.boritjjaroo.gflib.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

class Skin {
    var skinDatas = mutableMapOf<Int, SkinData>()

    fun loadSkinInfo(data: ByteArray) {
        skinDatas.clear()
        try {
            val reader = BufferedReader(InputStreamReader(ByteArrayInputStream(data), Charsets.UTF_8))
            val json = JsonParser.parseReader(reader).asJsonObject
            val entrySet = json.entrySet()
            for (entry in entrySet) {
                val skinData = SkinData(entry.value.asJsonObject)
                skinDatas[skinData.id] = skinData
            }
        } catch (e: Exception) {

        }
    }
    //"skin_with_user_info": {
    //    "201": { "skin_id": "201", "user_id": "1870807", "is_read": "0" },
    //    "202": { "skin_id": "202", "user_id": "1870807", "is_read": "0" }
    //}
    fun generateJsonSkinWithUserInfo() : JsonObject {
        val json = JsonObject()
        val entrySet = skinDatas.entries
        for (entry in entrySet) {
            val jsonSkin = entry.value.generateJsonSkinWithUserInfo()
            json.add(entry.key.toString(), jsonSkin)
        }
        //Log.v(GFUtil.TAG, "json : \n$json")
        return json
    }
}