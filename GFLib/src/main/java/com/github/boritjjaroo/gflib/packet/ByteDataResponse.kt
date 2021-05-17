package com.github.boritjjaroo.gflib.packet

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData

class ByteDataResponse : GfPacket() {
    override fun process(data: ByteArray) {
        super.process(data)

        //Log.i(GFUtil.TAG, "ByteDataResponse:process()")
        Log.i(GFUtil.TAG, "data :\n" + GFUtil.byteArrayToUTF8(data))

        if (isEncrypted(data)) {
            val byteArray = GfData.session.decryptGFDataRaw(data)
            Log.i(GFUtil.TAG, "decrypted : " + GFUtil.byteArrayToUTF8(byteArray))
        }
    }
}
