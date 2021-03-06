package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import com.github.boritjjaroo.gflib.data.GfData
import com.google.gson.JsonElement

open class GfPacket {

    fun isEncrypted(data: ByteArray) : Boolean {
        return data[0] == '#'.code.toByte()
    }

    fun getJsonFromOutdatacode(uri: Uri) : JsonElement? {
        val outdatacode = uri.getQueryParameter("outdatacode") ?: return null
        return GfData.session.decryptGFData(outdatacode.toByteArray())
    }
}