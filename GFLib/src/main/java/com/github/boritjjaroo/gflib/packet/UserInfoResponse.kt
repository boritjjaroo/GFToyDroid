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

        if (GfData.options.injectAllSkins()) {

            // Load private adjutant info
            val adjutantMultiData = GfData.repository.getAdjutantMulti()
            if (adjutantMultiData != null) {
                GfData.adjutantMulti.parseJsonString(adjutantMultiData)
                val jsonAdjutant = GfData.adjutantMulti.generateJsonUserAdjutantMulti()
                json.add("user_adjutant_multi", jsonAdjutant)
                Log.i(GFUtil.TAG, "Load private adjutant info.")
            }

            // Inject all skin data to user_info
            val newJsonSkin = GfData.skin.generateJsonSkinWithUserInfo()
            json.add("skin_with_user_info", newJsonSkin)
            GfData.isAllSkinInjected = true
            Log.i(GFUtil.TAG, "All skin infos are injected.")

            // Encrypt Json
            val modifiedData = GfData.session.encrpytGFData(json.toString(), true, true)
            return modifiedData
        }

        return null
    }
}
