package com.github.boritjjaroo.gflib.data

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

class Target {
    class TargetData() {
        var orderId: Int = 0
        var enemyTeamId: Int = 0

        constructor(json: JsonObject) : this() {
            this.orderId = json.get("order_id").asInt
            this.enemyTeamId = json.get("enemy_team_id").asInt
        }
    }

    var targetDatas = mutableMapOf<Int, ArrayList<TargetData>>()

    fun loadTargetData(data: ByteArray) {
        targetDatas.clear()
        try {
            val reader = BufferedReader(InputStreamReader(ByteArrayInputStream(data), Charsets.UTF_8))
            val json = JsonParser.parseReader(reader).asJsonObject
            val entrySet = json.entrySet()
            for (entry in entrySet) {
                var jsonArray = entry.value.asJsonArray
                var targets = arrayListOf<TargetData>()
                for (item in jsonArray) {
                    val targetData = TargetData(item.asJsonObject)
                    targets.add(targetData)
                }
                var id = entry.key.toInt()
                targetDatas[id] = targets
            }
        } catch (e: Exception) {

        }
    }

    fun generateJsonTargettrainCollectUserInfo() : JsonElement {
        val jsonTargets = JsonArray()
        for (target in targetDatas.entries.elementAt(0).value) {
            val json = JsonObject()
            json.addProperty("user_id", GfData.userInfo.userId)
            json.addProperty("order_id", target.orderId.toString())
            json.addProperty("enemy_team_id", target.enemyTeamId.toString())
            json.addProperty("is_lock", "0")
            jsonTargets.add(json)
        }
        return jsonTargets
    }
}