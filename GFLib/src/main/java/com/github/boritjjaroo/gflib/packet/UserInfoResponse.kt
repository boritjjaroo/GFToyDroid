package com.github.boritjjaroo.gflib.packet

import com.github.boritjjaroo.gflib.data.GfData

class UserInfoResponse : GfResponsePacket() {

    companion object {
        val ID = "Index/index"
    }

    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        GfData.log.v("UserInfoResponse:process()")

        assert(isEncrypted(data))
        var modified = false
        val json = GfData.session.decryptGFData(data)

        //json.remove("item_with_user_info")
        //json.remove("equip_with_user_info")
        //json.remove("furniture_collect_info")
        //json.remove("fairy_with_user_info")
        //json.remove("gun_with_user_info")
        //json.remove("outhouse_establish_info")
        //json.remove("chip_with_user_info")
        //json.remove("sangvis_with_user_info")
        //GFUtil.logV("json :\n$json")

        GfData.userInfo.parseJson(json)
        GfData.adjutantMulti.parseJsonUserInfo(json)
        GfData.gun.parseJsonUserInfo(json)
        GfData.skin.parseJsonUserInfo(json)
        GfData.hexie = json.get("hexie")?.asInt ?: 0

        if (GfData.options.usePrivateGunSkin()) {
            val map = GfData.gun.loadPrivateGunSkin()
            if (map.isNotEmpty()) {
                val jsonGuns = json.getAsJsonArray("gun_with_user_info")
                jsonGuns.forEach { jsonGun ->
                    val id = jsonGun.asJsonObject.get("id").asInt
                    val privateSkinId = map[id]
                    if (privateSkinId != null) {
                        jsonGun.asJsonObject.addProperty("skin", privateSkinId.toString())
                    }
                }
                modified = true
            }
        }

        if (GfData.options.usePrivateAdjutant()) {
            // Load private adjutant info
            val adjutantMultiData = GfData.repository.getAdjutantMulti()
            if (adjutantMultiData != null) {
                GfData.adjutantMulti.parseJsonString(adjutantMultiData)
                val jsonAdjutant = GfData.adjutantMulti.generateJsonUserAdjutantMulti()
                json.add("user_adjutant_multi", jsonAdjutant)
                GfData.log.i("Load private adjutant info.")
                modified = true
            }
        }

        if (GfData.options.injectAllSkins()) {
            // Inject all skin data to user_info
            val newJsonSkin = GfData.skin.generateJsonSkinWithUserInfo()
            json.add("skin_with_user_info", newJsonSkin)
            GfData.isAllSkinInjected = true
            modified = true
            GfData.log.i("All skin infos are injected.")
        }

        if (GfData.options.releaseCensorship()) {
            json.addProperty("naive_build_gun_formula", "30:30:30:30")
            GfData.log.i("\"naive_build_gun_formula\":\"30:30:30:30\" injected.")
            modified = true
        }

        if (GfData.options.injectTargetInfo()) {
            val newTarget = GfData.target.generateJsonTargettrainCollectUserInfo();
            json.add("targettrain_collect_user_info", newTarget)
            modified = true
            GfData.log.i("[] target data is injected.")
        }

        if (modified) {
            // Encrypt modified Json
            val modifiedData = GfData.session.encrpytGFData(json.toString(), true, true)
            return modifiedData
        }

        return null
    }
}
