package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData

class AdjutantRequest : GfRequestPacket() {

    companion object {
        val ID = "Index/adjutant"
    }

    override fun processHeader(): Int {
        return if (GfData.isAllSkinInjected) 1 else 0
    }

    override fun processBody(data: ByteArray) : ByteArray? {
        super.processBody(data)

        Log.v(GFUtil.TAG, "AdjutantRequest:process()")

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
                Log.i(GFUtil.TAG, "Private adjutant info are saved.")

                // Send user_info's adjutant_multi data instead
                val reqId = uri.getQueryParameter("req_id")
                if (reqId != null) {
                    val newQuery = generateFakeQueryData(reqId)
                    Log.i(GFUtil.TAG, "Changed to user_info's adjutant_multi data.")
                    return newQuery.toByteArray()
                }
            } catch (e: Exception) {
            }
        }

        return null
    }

    private fun generateFakeQueryData(reqId: String) : String {
        val newJsonStr = "{\"adjutant_multi\":\"${GfData.adjutantMulti.requestString}\"}"
        val newData = GfData.session.encrpytGFData(newJsonStr, false, false)
        val queryStr = "uid=${GfData.userInfo.userId}&outdatacode=dummy&req_id=$reqId"
        val newQuery = replaceParam(queryStr, "outdatacode", String(newData))
        return newQuery
    }
}