package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import com.github.boritjjaroo.gflib.data.GfData
import com.github.boritjjaroo.gflib.data.GfLog

class ChangeSkinRequest : GfRequestPacket() {
    companion object {
        val ID = "Dorm/changeSkin"
    }

    override fun processHeader() : Int {
        return if (GfData.isAllSkinInjected) 1 else 0
    }

    override fun processBody(data: ByteArray) : ByteArray? {
        super.processBody(data)

        GfData.log.v("ChangeSkinRequest:processBody()")

        // Block change
        // Send original(unchanged) data instead
        if (GfData.isAllSkinInjected) {
            try {
                // decrypt original data to find out timestamp value
                val uri = Uri.parse("http://dummy.host/path?" + String(data))
                val outdatacode = uri.getQueryParameter("outdatacode")
                // {"gun_with_user_id":329900471,"skin_id":0}
                val json = GfData.session.decryptGFData(outdatacode!!.toByteArray())
                val gunId = json.get("gun_with_user_id").asInt
                val skinId = json.get("skin_id").asInt
                GfData.gun.replaceGunSkinAndSave(gunId, skinId)
                GfData.log.i("Private gun's skin info are saved.")

                // Send user_info's gun_with_user_info data instead
                val reqId = uri.getQueryParameter("req_id")
                if (reqId != null) {
                    val originalSkinId = GfData.gun.getGunSkin(gunId)
                    val newJsonStr = "{\"gun_with_user_id\":$gunId,\"skin_id\":$originalSkinId}"
                    val newQuery = generateFakeQueryData(newJsonStr, reqId)
                    GfData.log.i("Changed to user_info's gun_skin data.", GfLog.TOAST)
                    return newQuery.toByteArray()
                }
            } catch (e: Exception) {
            }
        }

        return null
    }
}
