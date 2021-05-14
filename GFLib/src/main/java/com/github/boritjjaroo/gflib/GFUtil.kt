package com.github.boritjjaroo.gflib

import java.nio.ByteBuffer

class GFUtil {
    companion object {
        fun byteBufferToHexString(buffer : ByteBuffer, count : Int) : String {
            var result = ""
            var byteCount = 0
            var buf = buffer.array()
            for (b in buf) {
                result += String.format("%02X ", b)
                byteCount++
                if (byteCount % 16 == 0)
                    result += "\n"
                if (count > 0 && byteCount >= count)
                    break
            }
            return result
        }

        fun byteBufferToUTF8(buffer : ByteBuffer) : String {
            var cs = Charsets.UTF_8
            return cs.decode(buffer).toString()
        }
    }
}