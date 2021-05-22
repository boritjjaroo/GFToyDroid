package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import com.github.boritjjaroo.gflib.data.GfData
import com.github.boritjjaroo.gflib.data.GfLog

class AdjutantRequest : GfRequestPacket() {

    companion object {
        val ID = "Index/adjutant"
    }

    override fun processHeader(): Int {
        return if (GfData.isAllSkinInjected) 1 else 0
    }

    override fun processBody(data: ByteArray) : ByteArray? {
        super.processBody(data)

        GfData.log.v("AdjutantRequest:processBody()")

        // Block adjutant change
        // Send original(unchanged) data instead
        if (GfData.isAllSkinInjected) {
            try {
                // decrypt original data to find out timestamp value
                val uri = Uri.parse("http://dummy.host/path?" + String(data))
                val outdatacode = uri.getQueryParameter("outdatacode")
                val json = GfData.session.decryptGFData(outdatacode!!.toByteArray())
                val jsonString = json.get("adjutant_multi").asString
                GfData.repository.putAdjutantMulti(jsonString)
                GfData.log.i("Private adjutant info are saved.")

                // Send user_info's adjutant_multi data instead
                val reqId = uri.getQueryParameter("req_id")
                if (reqId != null) {
                    val newJsonStr = "{\"adjutant_multi\":\"${GfData.adjutantMulti.requestString}\"}"
                    val newQuery = generateFakeQueryData(newJsonStr, reqId)
                    GfData.log.i("Changed to user_info's adjutant_multi data.", GfLog.TOAST)
                    return newQuery.toByteArray()
                }
            } catch (e: Exception) {
            }
        }

        return null
    }
}