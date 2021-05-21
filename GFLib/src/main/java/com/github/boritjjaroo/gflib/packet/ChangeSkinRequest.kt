package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData

class ChangeSkinRequest : GfRequestPacket() {
    companion object {
        val ID = "Dorm/changeSkin"
    }

    override fun processHeader() : Int {
        return if (GfData.isAllSkinInjected) 1 else 0
    }

    override fun processBody(data: ByteArray) : ByteArray? {
        super.processBody(data)

        Log.v(GFUtil.TAG, "ChangeSkinRequest:process()")

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
                Log.i(GFUtil.TAG, "Private gun's skin info are saved.")

                // Send user_info's gun_with_user_info data instead
                val reqId = uri.getQueryParameter("req_id")
                if (reqId != null) {
                    val originalSkinId = GfData.gun.getGunSkin(gunId)
                    val newJsonStr = "{\"gun_with_user_id\":$gunId,\"skin_id\":$originalSkinId}"
                    val newData = GfData.session.encrpytGFData(newJsonStr, false, false)
                    val queryStr = "uid=${GfData.userInfo.userId}&outdatacode=dummy&req_id=$reqId"
                    val newQuery = replaceParam(queryStr, "outdatacode", String(newData))
                    Log.i(GFUtil.TAG, "Changed to user_info's gun_skin data.")
                    return newQuery.toByteArray()
                }
            } catch (e: Exception) {
            }
        }

        return null
    }
}
