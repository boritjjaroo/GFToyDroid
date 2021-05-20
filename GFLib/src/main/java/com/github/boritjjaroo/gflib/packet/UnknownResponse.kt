package com.github.boritjjaroo.gflib.packet

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData

class UnknownResponse : GfResponsePacket() {
    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        //Log.v(GFUtil.TAG, "UnknownResponse:process()")
        Log.v(GFUtil.TAG, "data :\n" + GFUtil.byteArrayToUTF8(data))

        if (isEncrypted(data)) {
            val json = GfData.session.decryptGFData(data)
            Log.v(GFUtil.TAG, "json :\n$json")
        }

        return null
    }
}
