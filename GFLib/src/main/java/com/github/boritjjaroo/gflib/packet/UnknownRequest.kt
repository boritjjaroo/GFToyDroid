package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData

class UnknownRequest : GfPacket() {
    override fun process(data: ByteArray) {
        super.process(data)

        //Log.i(GFUtil.TAG, "UnknownRequest:process()")
        Log.i(GFUtil.TAG, "params :\n" + GFUtil.byteArrayToUTF8(data))

        val uri = Uri.parse("http://dummy.host/path?" + GFUtil.byteArrayToUTF8(data))
        val signcode = uri.getQueryParameter("signcode")
        if (signcode != null) {
            val byteArray = GfData.session.decryptGFDataRaw(signcode.toByteArray())
            Log.i(GFUtil.TAG, "signcode : " + GFUtil.byteArrayToUTF8(byteArray))
        }
        val outdatacode = uri.getQueryParameter("outdatacode")
        if (outdatacode != null) {
            val json = GfData.session.decryptGFData(outdatacode.toByteArray())
            Log.i(GFUtil.TAG, "outdatacode :\n$json")
        }
    }
}