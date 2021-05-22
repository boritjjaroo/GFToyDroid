package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import com.github.boritjjaroo.gflib.data.GfData
import com.github.boritjjaroo.gflib.data.GfLog

// Request format
// http://gf-game.girlfrontline.co.kr/index.php/1001/Index/crashLog
// uid=1870807&outdatacode=encrypted(no#)&req_id=162165728100035
//{
//    "context": "尝试加载反和谐Live2D\n\nstart motion daiji_idle_01\n\nHit hitArea:leg\n\nstart motion touch_5\n\n尝试加载反和谐Live2D\n\nstart motion daiji_idle_01\n\nEnqueue: Dorm/changeSkin\t{\"gun_with_user_id\":419387148,\"skin_id\":5101}\nhttp://gf-game.girlfrontline.co.kr/index.php/1001/\n\nDequeue: Dorm/changeSkin\t{\"gun_with_user_id\":419387148,\"skin_id\":5101}\n162165728100034\n\n实例化数目0销毁数目0\n\nFunction：Dorm/changeSkin\tReceive: error:2\n\nDorm/changeSkin\nerror:2\n\n\n",
//    "condition": "Dorm/changeSkin\n",
//    "stackTrace": "error:2\n",
//    "clientVersion": "2.0710_294"
//}

class CrashLogRequest : GfRequestPacket() {

    companion object {
        val ID = "Index/crashLog"
    }

    override fun processHeader(): Int {
        return 1
    }

    override fun processBody(data: ByteArray) : ByteArray? {
        super.processBody(data)

        GfData.log.v("CrashLogRequest:processBody()")

        try {
            // decrypt original data to find out timestamp value
            val uri = Uri.parse("http://dummy.host/path?" + String(data))
            val outdatacode = uri.getQueryParameter("outdatacode")
            val jsonStr = GfData.session.decryptGFDataRaw(outdatacode!!.toByteArray())
            GfData.log.w(String(jsonStr), GfLog.FORCE)

            // Send empty json
            val reqId = uri.getQueryParameter("req_id")
            if (reqId != null) {
                val newQuery = generateFakeQueryData("{}", reqId)
                GfData.log.i("Changed to empty crash data.", GfLog.FORCE or GfLog.TOAST)
                return newQuery.toByteArray()
            }
        } catch (e: Exception) {
        }

        return null
    }
}