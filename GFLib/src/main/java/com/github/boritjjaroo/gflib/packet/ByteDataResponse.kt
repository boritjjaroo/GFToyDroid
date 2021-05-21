package com.github.boritjjaroo.gflib.packet

import com.github.boritjjaroo.gflib.data.GfData

class ByteDataResponse : GfResponsePacket() {
    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        if (!GfData.options.logUnknownPacketData())
            return null

        GfData.log.v("ByteDataResponse:process()")
        GfData.log.v("data :\n" + String(data))

        if (isEncrypted(data)) {
            val byteArray = GfData.session.decryptGFDataRaw(data)
            GfData.log.v("decrypted : " + String(byteArray))
        }
        return null
    }
}
