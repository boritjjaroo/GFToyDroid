package com.github.boritjjaroo.gflib.packet

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData
import com.google.gson.JsonObject

class GetUid : GfPacket() {

    companion object {
        val ID = "Index/getUidTianxiaQueue"
    }

    override fun process(data: ByteArray) {
        super.process(data)

        //Log.i(GFUtil.TAG, "GetUid:process()")

        if (isEncrypted(data)) {
            val json: JsonObject = GfData.session.decryptGFData(data)
            Log.i(GFUtil.TAG, "json :\n" + json.toString())

            GfData.session.sign = json["sign"].asString
            Log.i(GFUtil.TAG, "new sign value :\n" + GfData.session.sign)
        }
        else {
            Log.i(GFUtil.TAG, "plain text :\n" + GFUtil.byteArrayToUTF8(data))
        }
    }
}