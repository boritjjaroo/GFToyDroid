package com.github.boritjjaroo.gflib.data

import android.text.format.DateFormat


object GfData {
    var session: Session = Session()

    lateinit var options: GfOptions
    lateinit var repository: GfDataRepository

    class DummyLog : GfLog {
        override fun put(priorityEx: Int, msg: String) {
        }
    }
    @kotlin.jvm.JvmField
    var log: GfLog = DummyLog()

    var userInfo: UserInfo = UserInfo()

    var gun: Gun = Gun()

    var skin: Skin = Skin()

    var target: Target = Target()

    var adjutantMulti: AdjutantMulti = AdjutantMulti()

    var friends: Friends = Friends()

    var isAllSkinInjected: Boolean = false

    // release censorship for china server
    var hexie: Int = 0

    fun init() {
        friends.init()
    }

    fun initSession() {
        userInfo.initSession()
        session.initSession()
        gun.initSession()
        skin.initSession()
        friends.initSession()
        isAllSkinInjected = false
        hexie = 0
    }

    fun log(priority: Int, msg: String) {
        log.put(priority, msg)
    }

    fun getStatus() : String {
        val loginTimeStr = DateFormat.format(
            "yyyy-MM-dd E a hh:mm:ss",
            userInfo.lastLoginTime * 1000L).toString()

        var str = ""
        str += "user_id\n  ${userInfo.userId}\n\n"
        str += "last_login_time\n  $loginTimeStr\n\n"
        str += "isAllSkinInjected\n  ${isAllSkinInjected}\n\n"
        return str
    }
}