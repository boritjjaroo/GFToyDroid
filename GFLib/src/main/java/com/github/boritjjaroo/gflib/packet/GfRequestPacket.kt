package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import com.github.boritjjaroo.gflib.data.GfData


open class GfRequestPacket : GfPacket() {

    open fun processHeader() : Int {
        return -1
    }

    open fun processBody(data: ByteArray) : ByteArray? {
        return null
    }

    fun replaceParam(query: String, key: String, value: String) : String {
        val uri = Uri.parse("http://dummy.host/path?$query")
        val params = uri.queryParameterNames
        val builder = uri.buildUpon().clearQuery()
        for (param in params) {
            builder.appendQueryParameter(
                param,
                if (param == key) value else uri.getQueryParameter(param)
            )
        }

        val newQuery = builder.build().encodedQuery
        if (newQuery == null) {
            GfData.log.e("Failed to replaceParam() so return original query!!!")
        }
        return newQuery ?: query
    }
}