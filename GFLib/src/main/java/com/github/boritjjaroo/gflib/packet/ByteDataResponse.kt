package com.github.boritjjaroo.gflib.packet

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData

class ByteDataResponse : GfPacket() {
    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        //Log.v(GFUtil.TAG, "ByteDataResponse:process()")
        Log.v(GFUtil.TAG, "data :\n" + GFUtil.byteArrayToUTF8(data))

        if (isEncrypted(data)) {
            val byteArray = GfData.session.decryptGFDataRaw(data)
            Log.v(GFUtil.TAG, "decrypted : " + GFUtil.byteArrayToUTF8(byteArray))
        }
        return null
    }
}
