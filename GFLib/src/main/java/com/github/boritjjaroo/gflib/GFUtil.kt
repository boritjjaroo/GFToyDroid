package com.github.boritjjaroo.gflib

import com.github.boritjjaroo.gflib.data.GfData
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

        fun logV(tag: String, str: String) {
            val MAX_LEN = 2000 // 2000 bytes 마다 끊어서 출력
            val len = str.length
            var idx = 0
            var nextIdx = 0
            while (idx < len) {
                nextIdx += MAX_LEN
                GfData.log.v(str.substring(idx, if (nextIdx > len) len else nextIdx))
                idx = nextIdx
            }
        }
    }
}