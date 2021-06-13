package com.github.boritjjaroo.gflib.packet

import com.github.boritjjaroo.gflib.data.GfData
import com.github.boritjjaroo.gflib.encryption.Sign
import com.google.gson.JsonObject

class GetUidResponse : GfResponsePacket() {

    companion object {
        val ID = "Index/getUidTianxiaQueue"
    }

    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        GfData.log.v("GetUidResponse:process()")

        if (isEncrypted(data)) {
            GfData.initSession()

            val json: JsonObject = GfData.session.decryptGFData(data) as JsonObject
            GfData.log.v("json :\n" + json.toString())

            GfData.session.sign = Sign(json["sign"].asString)
            GfData.log.v("new sign value : " + GfData.session.sign)
        }

        return null
    }
}