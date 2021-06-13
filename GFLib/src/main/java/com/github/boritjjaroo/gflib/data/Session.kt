package com.github.boritjjaroo.gflib.data

import com.github.boritjjaroo.gflib.encryption.GfEncryptionInputStream
import com.github.boritjjaroo.gflib.encryption.GfEncryptionOutputStream
import com.github.boritjjaroo.gflib.encryption.Sign
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.*
import java.util.*

class Session() {
    lateinit var sign: Sign
    lateinit var date: Date
    lateinit var reqId: String

    init {
        initSession()
    }

    fun initSession() {
        sign = Sign("yundoudou")
        date = Date()
        reqId = "162098608400008"
    }

    fun decryptGFData(gfData: ByteArray) : JsonElement {
        return decryptGFData(gfData, this.sign)
    }

    fun decryptGFData(gfData: ByteArray, sign: Sign) : JsonElement {

        var data: JsonElement = JsonObject()

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
            val inputStream = GfEncryptionInputStream(byteArrayInputStream, sign)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            data = JsonParser.parseReader(bufferedReader)
            this.date = inputStream.date
        } catch (e: Exception) {
            GfData.log.w("Session::decryptGFData() failed")
            GfData.log.w(e.toString())
        }

        return data
    }

    fun decryptGFDataRaw(gfData: ByteArray) : ByteArray {
        return decryptGFDataRaw(gfData, this.sign)
    }

    fun decryptGFDataRaw(gfData: ByteArray, sign: Sign) : ByteArray {

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
            val inputStream = GfEncryptionInputStream(byteArrayInputStream, sign)
            byteArray = inputStream.readBytes()
            inputStream.close()
            this.date = inputStream.date
        } catch (e: Exception) {
            GfData.log.w("Session::decryptGFData() failed")
            GfData.log.w(e.toString())
        }

        return byteArray
    }

    fun encrpytGFData(str: String, compress: Boolean, beginWithSharp: Boolean) : ByteArray {
        return encrpytGFData(str, compress, beginWithSharp, this.sign, this.date)
    }

    fun encrpytGFData(str: String, compress: Boolean, beginWithSharp: Boolean, sign: Sign, date: Date) : ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        if (beginWithSharp) {
            byteArrayOutputStream.write(byteArrayOf('#'.code.toByte()))
            byteArrayOutputStream.flush()
        }
        val outputStream = GfEncryptionOutputStream(byteArrayOutputStream)
        val writer = OutputStreamWriter(outputStream)
        writer.write(str)
        writer.flush()
        outputStream.finish(date, sign, compress)
        outputStream.flush()
        val byteArray = byteArrayOutputStream.toByteArray()
        writer.close()
        outputStream.close()
        byteArrayOutputStream.close()

        return byteArray
    }

}