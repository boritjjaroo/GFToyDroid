package com.github.boritjjaroo.gflib.packet

open class GfResponsePacket : GfPacket() {
    open fun process(data: ByteArray) : ByteArray? {
        return null
    }
}