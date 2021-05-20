package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData

class UnknownRequest : GfRequestPacket() {
    override fun processBody(data: ByteArray) : ByteArray? {
        super.processBody(data)

        //Log.v(GFUtil.TAG, "UnknownRequest:process()")
        Log.v(GFUtil.TAG, "params :\n" + GFUtil.byteArrayToUTF8(data))

        val uri = Uri.parse("http://dummy.host/path?" + GFUtil.byteArrayToUTF8(data))
        val reqId = uri.getQueryParameter("req_id")
        if (reqId != null) {
            GfData.session.reqId = reqId
        }
        val signcode = uri.getQueryParameter("signcode")
        if (signcode != null) {
            val byteArray = GfData.session.decryptGFDataRaw(signcode.toByteArray())
            Log.v(GFUtil.TAG, "signcode : " + GFUtil.byteArrayToUTF8(byteArray))
        }
        val outdatacode = uri.getQueryParameter("outdatacode")
        if (outdatacode != null) {
            val json = GfData.session.decryptGFData(outdatacode.toByteArray())
            Log.v(GFUtil.TAG, "outdatacode :\n$json")
        }

        return null
    }
}