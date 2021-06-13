package com.github.boritjjaroo.gflib.packet

import com.github.boritjjaroo.gflib.data.GfData
import com.google.gson.JsonObject

class FriendUserCardResponse : GfResponsePacket() {

    companion object {
        val ID = "Friend/usercard"
    }

    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        GfData.log.v("FriendUserCardResponse:process()")

        //val json = GfData.session.decryptGFData(data)
        //GfData.log.v("json : \n${json.toString()}")

        if (GfData.options.displayFriendInfo()) {
            if (isEncrypted(data)) {
                val json = GfData.session.decryptGFData(data) as JsonObject
                val id = json.get("f_userid").asInt
                val info = GfData.friends.get(id)
                if (info != null) {
                    var intro = json.get("intro").asString
                    val newIntro = "[${info.dormCount}][${info.getLastVisitString()}]$intro"
                    json.addProperty("intro", newIntro)
                    val modifiedData = GfData.session.encrpytGFData(json.toString(), true, true)
                    GfData.log.i("User-card intro is modified.")
                    return modifiedData
                }
            }
        }
        return null
    }
}
