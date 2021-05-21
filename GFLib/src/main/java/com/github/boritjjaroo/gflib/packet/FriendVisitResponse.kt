package com.github.boritjjaroo.gflib.packet

import com.github.boritjjaroo.gflib.data.GfData

class FriendVisitResponse : GfResponsePacket() {

    companion object {
        val ID = "Friend/visit"
    }

    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        GfData.log.v("FriendVisitResponse:process()")

        if (GfData.options.displayDormitoryBattery()) {
            if (isEncrypted(data)) {
                val json = GfData.session.decryptGFData(data)
                val coin = json.get("build_coin_flag").asInt
                var notice = ""
                val jsonNotice = json.getAsJsonObject("notice")
                if (jsonNotice.get("notice") != null) {
                    notice = jsonNotice.get("notice").asString
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
