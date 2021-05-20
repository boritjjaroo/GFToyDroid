package com.github.boritjjaroo.gflib.packet

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData
import com.github.boritjjaroo.gflib.encryption.Sign
import com.google.gson.JsonObject

class GetUidResponse : GfResponsePacket() {

    companion object {
        val ID = "Index/getUidTianxiaQueue"
    }

    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        //Log.v(GFUtil.TAG, "GetUid:process()")

        if (isEncrypted(data)) {
            GfData.init()

            val json: JsonObject = GfData.session.decryptGFData(data)
            Log.v(GFUtil.TAG, "json :\n" + json.toString())

            GfData.session.sign = Sign(json["sign"].asString)
            Log.v(GFUtil.TAG, "new sign value : " + GfData.session.sign)
        }

        return null
    }
}