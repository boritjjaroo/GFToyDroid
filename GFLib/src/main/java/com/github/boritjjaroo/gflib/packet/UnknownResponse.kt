package com.github.boritjjaroo.gflib.packet

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData

class UnknownResponse : GfPacket() {
    override fun process(data: ByteArray) {
        super.process(data)

        //Log.i(GFUtil.TAG, "UnknownResponse:process()")
        Log.i(GFUtil.TAG, "data :\n" + GFUtil.byteArrayToUTF8(data))

        if (isEncrypted(data)) {
            val json = GfData.session.decryptGFData(data)
            Log.i(GFUtil.TAG, "json :\n" + json.toString())
        }
    }
}
