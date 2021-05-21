package com.github.boritjjaroo.gflib.data

import java.util.*


object GfData {
    var session: Session = Session()

    lateinit var options: GfOptions
    lateinit var repository: GfDataRepository

    class DummyLog : GfLog {
        override fun put(priority: Int, msg: String) {
        }
    }
    @kotlin.jvm.JvmField
    var log: GfLog = DummyLog()

    var userInfo: UserInfo = UserInfo()

    var gun: Gun = Gun()

    var skin: Skin = Skin()

    var adjutantMulti: AdjutantMulti = AdjutantMulti()

    var isAllSkinInjected: Boolean = false

    // release censorship for china server
    var hexie: Int = 0

    fun init() {
        userInfo.init()
        session.init()
        gun.init()
        skin.init()
        isAllSkinInjected = false
        hexie = 0
    }

    fun log(priority: Int, msg: String) {
        log.put(priority, msg)
    }

    fun getStatus() : String {
        var str = ""
        str += "user_id\n  ${userInfo.userId}\n\n"
        str += "last_login_time\n  ${Date(userInfo.lastLoginTime)}\n\n"
        str += "isAllSkinInjected\n  ${isAllSkinInjected}\n\n"
        return str
    }
}