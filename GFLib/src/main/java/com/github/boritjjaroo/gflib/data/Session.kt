package com.github.boritjjaroo.gflib.data

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.encryption.GfEncryptionInputStream
import com.github.boritjjaroo.gflib.encryption.GfEncryptionOutputStream
import com.github.boritjjaroo.gflib.encryption.Sign
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.*
import java.util.*

class Session {
    var sign: Sign = Sign("yundoudou")
    var date: Date = Date()

    fun decryptGFData(gfData: ByteArray) : JsonObject {
        //Log.i(GFUtil.TAG, "gfData : \n" + GFUtil.byteBufferToUTF8(ByteBuffer.wrap(gfData)))

        var data = JsonObject()

        // check if gfData's first is '#'
        var startIndex = 0
        if (gfData[0] == '#'.code.toByte()) {
            startIndex = 1
        }

        // check base64 padding
        var gfDataPadded = gfData
        val padding = (gfData.size - 1) % 4
        if (0 < padding) {
            val paddingByteArray = byteArrayOf(0x3D,0x3D,0x3D,0x3D)
            val paddedByteArray = gfData + paddingByteArray.copyOf(4 - padding)
            gfDataPadded = paddedByteArray
        }

        try {
            val byteArrayInputStream = ByteArrayInputStream(gfDataPadded, startIndex, gfDataPadded.size - startIndex)
            val inputStream = GfEncryptionInputStream(byteArrayInputStream, this.sign)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            data = JsonParser.parseReader(bufferedReader) as JsonObject
            this.date = inputStream.date
        } catch (e: Exception) {
            Log.w(GFUtil.TAG, "Session::decryptGFData() failed")
            Log.e(GFUtil.TAG, e.toString())
            Log.e(GFUtil.TAG, e.stackTraceToString())
        }

        return data
    }

    fun decryptGFDataRaw(gfData: ByteArray) : ByteArray {
        //Log.i(GFUtil.TAG, "gfData : \n" + GFUtil.byteBufferToUTF8(ByteBuffer.wrap(gfData)))

        var byteArray = ByteArray(1)

        // check if gfData's first is '#'
        var startIndex = 0
        if (gfData[0] == '#'.code.toByte()) {
            startIndex = 1
        }

        // check base64 padding
        var gfDataPadded = gfData
        val padding = (gfData.size - 1) % 4
        if (0 < padding) {
            val paddingByteArray = byteArrayOf(0x3D,0x3D,0x3D,0x3D)
            val paddedByteArray = gfData + paddingByteArray.copyOf(4 - padding)
            gfDataPadded = paddedByteArray
        }

        try {
            val byteArrayInputStream = ByteArrayInputStream(gfDataPadded, startIndex, gfDataPadded.size - startIndex)
            val inputStream = GfEncryptionInputStream(byteArrayInputStream, this.sign)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            byteArray = inputStream.readBytes()
            inputStream.close()
        } catch (e: Exception) {
            Log.w(GFUtil.TAG, "Session::decryptGFData() failed")
            Log.e(GFUtil.TAG, e.toString())
            Log.e(GFUtil.TAG, e.stackTraceToString())
        }

        return byteArray
    }

    fun encrpytGFData(json: JsonObject, compress: Boolean) : ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        byteArrayOutputStream.write(byteArrayOf('#'.code.toByte()))
        byteArrayOutputStream.flush()
        val outputStream = GfEncryptionOutputStream(byteArrayOutputStream)
        val writer = OutputStreamWriter(outputStream)
        writer.write(json.toString())
        writer.flush()
        outputStream.finish(this.date, this.sign, compress)
        outputStream.flush()
        val byteArray = byteArrayOutputStream.toByteArray()
        writer.close()
        outputStream.close()
        byteArrayOutputStream.close()

        return byteArray
    }
}