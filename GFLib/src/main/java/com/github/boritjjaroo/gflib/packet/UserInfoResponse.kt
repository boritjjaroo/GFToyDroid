package com.github.boritjjaroo.gflib.packet

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData

class UserInfoResponse : GfResponsePacket() {

    companion object {
        val ID = "Index/index"
    }

    override fun process(data: ByteArray) : ByteArray? {
        super.process(data)

        //Log.v(GFUtil.TAG, "UserInfoResponse:process()")
        //Log.v(GFUtil.TAG, "data :\n" + GFUtil.byteArrayToUTF8(data))

        assert(isEncrypted(data))
        val json = GfData.session.decryptGFData(data)
        //Log.v(GFUtil.TAG, "json :\n$json")

        GfData.userInfo.parseJson(json)
        GfData.adjutantMulti.parseJsonUserInfo(json)
        GfData.skin.parseJsonUserInfo(json)
        Log.v(GFUtil.TAG, "adjutant_multi : \n${GfData.adjutantMulti.requestString}")

        if (GfData.options.injectAllSkins()) {
            val newJsonSkin = GfData.skin.generateJsonSkinWithUserInfo()
            json.add("skin_with_user_info", newJsonSkin)
            val modifiedData = GfData.session.encrpytGFData(json.toString(), true, true)
            GfData.isAllSkinInjected = true
            Log.i(GFUtil.TAG, "All skin infos are injected.")
            return modifiedData
        }

        return null
    }
}
