package com.github.boritjjaroo.gflib.packet

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData

class FriendVisitResponse : GfResponsePacket() {

    companion object {
        val ID = "Friend/visit"
    }

    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        if (GfData.options.displayDorimitoryBattery()) {
            if (isEncrypted(data)) {
                val json = GfData.session.decryptGFData(data)
                //GFUtil.logV(GFUtil.TAG, "json :\n" + json.toString())
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
                Log.i(GFUtil.TAG, "Dormitory notice is modified.")
                return modifiedData
            }
        }
        return null
    }
}
