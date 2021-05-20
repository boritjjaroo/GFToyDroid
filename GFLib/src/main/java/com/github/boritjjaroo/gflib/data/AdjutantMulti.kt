package com.github.boritjjaroo.gflib.data

import com.google.gson.JsonArray
import com.google.gson.JsonObject

class AdjutantMulti {

    val adjutants: ArrayList<Adjutant> = ArrayList<Adjutant>()
    var requestString = ""

    class Adjutant(data: String) {
        var index: Int
        // 0 : Others, 1 : Figurin, 2 : Fairy, 3 : Coalition
        var targetType: Int
        var targetId: Int
        // 0 : default(no costume)
        var skinId: Int
        var isSexy: Int
        var mod: Int

        init {
            val values = data.split('|')
            if (values.size == 7) {
                this.index = values[1].toInt()
                this.targetType = values[2].toInt()
                this.targetId = values[3].toInt()
                this.skinId = values[4].toInt()
                this.isSexy = values[5].toInt()
                this.mod = values[6].toInt()
            }
            else {
                this.index = 0
                this.targetType = 0
                this.targetId = 0
                this.skinId = 0
                this.isSexy = 0
                this.mod = 0
            }
        }
    }

    // "single|0|1|307|5603|0|0,single|1|1|94|5608|1|0,single|2|1|270|5305|1|0,single|3|1|49|5508|1|0,
    // combined|0|1|153|5605|0|0,combined|1|0|0|0|0|0"
    fun parseJsonString(jsonStr: String) {
        adjutants.clear()
        jsonStr.split(',').forEach { item -> adjutants.add(Adjutant(item)) }
    }

    fun generateJsonString() : String {
        var jsonStr = ""
        for ((index, adjutant) in adjutants.withIndex()) {
            jsonStr += String.format(
                "%s|%d|%d|%d|%d|%d|%d",
                { if (index < 4) "single" else "combined" },
                adjutant.index,
                adjutant.targetType,
                adjutant.targetId,
                adjutant.skinId,
                adjutant.isSexy,
                adjutant.mod,
            )
            if (index < adjutants.size - 1) jsonStr += ","
        }
        return jsonStr
    }

    fun generateJsonUserAdjutantMulti() : JsonObject {
        val jsonSingle = JsonArray()
        val jsonCombined = JsonArray()
        for ((index, adjutant) in adjutants.withIndex()) {
            val jsonItem = JsonObject()
            jsonItem.addProperty("index", adjutant.index.toString())
            jsonItem.addProperty("target_type", adjutant.targetType.toString())
            jsonItem.addProperty("target_id", adjutant.targetId.toString())
            jsonItem.addProperty("skin_id", adjutant.skinId.toString())
            jsonItem.addProperty("is_sexy", adjutant.isSexy.toString())
            jsonItem.addProperty("mod", adjutant.mod.toString())
            if (index < 4) {
                jsonSingle.add(jsonItem)
            }
            else {
                jsonCombined.add(jsonItem)
            }
        }

        val json = JsonObject()
        json.add("single", jsonSingle)
        json.add("combined", jsonCombined)

        return json
    }

    fun parseJsonUserInfo(json: JsonObject) {
        val jsonUserRecord = json.get("user_record").asJsonObject
        requestString = jsonUserRecord.get("adjutant_multi").asString
        parseJsonString(requestString)
    }
}