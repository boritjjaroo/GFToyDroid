package com.github.boritjjaroo.gflib

import android.util.Log
import java.nio.ByteBuffer

class GFUtil {
    companion object {

        val TAG = "GFLib"

        fun byteArrayToHexString(buffer: ByteArray, count: Int) : String {
            var result = ""
            var byteCount = 0
            for (b in buffer) {
                result += String.format("%02X ", b)
                byteCount++
                if (byteCount % 16 == 0)
                    result += "\n"
                if (count > 0 && byteCount >= count)
                    break
            }
            return result
        }

        fun byteArrayToUTF8(byteArray: ByteArray) : String {
            val cs = Charsets.UTF_8
            return cs.decode(ByteBuffer.wrap(byteArray)).toString()
        }

        fun logV(tag: String, str: String) {
            val MAX_LEN = 2000 // 2000 bytes 마다 끊어서 출력
            val len = str.length
            var idx = 0
            var nextIdx = 0
            while (idx < len) {
                nextIdx += MAX_LEN
                Log.v(tag, str.substring(idx, if (nextIdx > len) len else nextIdx))
                idx = nextIdx
            }
        }
    }
}