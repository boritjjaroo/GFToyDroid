package com.github.boritjjaroo.gflib.packet

import com.github.boritjjaroo.gflib.data.GfData

class FriendDormInfoResponse : GfResponsePacket() {

    companion object {
        val ID = "Friend/dormInfo"
    }

    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        GfData.log.v("FriendDormInfoResponse:process()")

        val json = GfData.session.decryptGFData(data)
        //GfData.log.v("json : \n${json.toString()}")

        val jsonArray = json.getAsJsonArray("in_mydorm_list")

        GfData.friends.beginUpdateLastVisit()
        for (jsonItem in jsonArray) {
            val id = jsonItem.asJsonObject?.get("f_userid")?.asInt ?: 0
            if (0 < id)
                GfData.friends.updateLastVisit(id)
        }
        GfData.friends.endUpdateLastVisit()

        GfData.log.v("Friend visit info updated")

        return null
    }
}
