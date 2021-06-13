package com.github.boritjjaroo.gflib.packet

import com.github.boritjjaroo.gflib.data.GfData

class FriendMessageListResponse : GfResponsePacket() {

    companion object {
        val ID = "Friend/messagelist"
    }

    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        GfData.log.v("FriendMessageListResponse:process()")

        //val json = GfData.session.decryptGFData(data)
        //GFUtil.logV("json : \n${json.toString()}")


        return null
    }
}
