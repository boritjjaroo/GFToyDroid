package com.github.boritjjaroo.gflib.packet

open class GfPacket {

    open fun process(data: ByteArray) {

    }

    fun isEncrypted(data: ByteArray) : Boolean {
        return data[0] == '#'.code.toByte()
    }
}