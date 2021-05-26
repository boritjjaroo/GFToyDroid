package com.github.boritjjaroo.gflib.packet

import com.github.boritjjaroo.gflib.data.GfData

class FriendVisitResponse : GfResponsePacket() {

    companion object {
        val ID = "Friend/visit"
    }

    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        GfData.log.v("FriendVisitResponse:process()")

        val json = GfData.session.decryptGFData(data)
        //GfData.log.v("json : \n${json.toString()}")

        // Update dorm count info from battery count
        val coin = json.get("build_coin_flag").asInt
        if (0 < coin) {
            val id = json.getAsJsonObject("info").get("f_userid").asInt
            GfData.friends.updateDormCount(id, coin)
        }

        if (GfData.options.displayFriendInfo()) {
            if (isEncrypted(data)) {
                var notice = ""
                val jsonNotice = json.getAsJsonObject("notice")
                val jsonNoticePrimitive = jsonNotice.getAsJsonPrimitive("notice")
                // CAUTION : case "notice":null is exist
                if (jsonNoticePrimitive.isString) {
                    notice = jsonNoticePrimitive.asString
                }
                val newNotice = "[$coin]$notice"
                // Dormitory notice force on
                jsonNotice.addProperty("is_view_notice", "1")
                jsonNotice.addProperty("notice", newNotice)
                val modifiedData = GfData.session.encrpytGFData(json.toString(), true, true)
                GfData.log.i("Dormitory notice is modified.")
                return modifiedData
            }
        }

        return null
    }
}
