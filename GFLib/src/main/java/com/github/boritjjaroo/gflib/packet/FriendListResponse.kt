package com.github.boritjjaroo.gflib.packet

import com.github.boritjjaroo.gflib.data.GfData

class FriendListResponse : GfResponsePacket() {

    companion object {
        val ID = "Friend/list"
    }

    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        GfData.log.v("FriendListResponse:process()")

        val json = GfData.session.decryptGFData(data)
        //GFUtil.logV("json : \n${json.toString()}")

        GfData.friends.beginUpdate()
        val jsonArray = json.getAsJsonArray("list")
        for (jsonItem in jsonArray) {
            val id = jsonItem.asJsonObject?.get("f_userid")?.asInt ?: 0
            if (0 < id)
                GfData.friends.update(id)
        }
        GfData.friends.endUpdate()
        GfData.log.v("Friend List updated")

        return null
    }
}
