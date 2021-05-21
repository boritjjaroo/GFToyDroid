package com.github.boritjjaroo.gflib.packet

import com.github.boritjjaroo.gflib.data.GfData

class UnknownResponse : GfResponsePacket() {
    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        if (!GfData.options.logUnknownPacketData())
            return null

        GfData.log.v("UnknownResponse:process()")
        GfData.log.v("data :\n" + String(data))

        if (isEncrypted(data)) {
            val json = GfData.session.decryptGFData(data)
            GfData.log.v("json :\n$json")
        }

        return null
    }
}
