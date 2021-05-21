package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import com.github.boritjjaroo.gflib.data.GfData

class UnknownRequest : GfRequestPacket() {
    override fun processBody(data: ByteArray) : ByteArray? {
        super.processBody(data)

        if (!GfData.options.logUnknownPacketData())
            return null

        GfData.log.v("UnknownRequest:process()")
        GfData.log.v("params :\n" + String(data))

        val uri = Uri.parse("http://dummy.host/path?" + String(data))
        val reqId = uri.getQueryParameter("req_id")
        if (reqId != null) {
            GfData.session.reqId = reqId
        }
        val signcode = uri.getQueryParameter("signcode")
        if (signcode != null) {
            val byteArray = GfData.session.decryptGFDataRaw(signcode.toByteArray())
            GfData.log.v("signcode : " + String(byteArray))
        }
        val outdatacode = uri.getQueryParameter("outdatacode")
        if (outdatacode != null) {
            val json = GfData.session.decryptGFData(outdatacode.toByteArray())
            GfData.log.v("outdatacode :\n$json")
        }

        return null
    }
}