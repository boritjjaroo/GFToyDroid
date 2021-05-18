package com.github.boritjjaroo.gflib.packet

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData

class UserInfoResponse : GfPacket() {

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

        if (GfData.options.modifyUserInfoAllSkins) {
            val newJsonSkin = GfData.skin.generateJsonSkinWithUserInfo()
            json.add("skin_with_user_info", newJsonSkin)
            val modifiedData = GfData.session.encrpytGFData(json, true)
            Log.i(GFUtil.TAG, "All skin infos are injected.")
            return modifiedData
        }

        return null
    }
}
