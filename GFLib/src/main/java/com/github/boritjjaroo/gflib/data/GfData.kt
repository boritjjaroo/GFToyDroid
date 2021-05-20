package com.github.boritjjaroo.gflib.data


object GfData {
    var session: Session = Session()

    lateinit var options: GfOptions
    lateinit var repository: GfDataRepository

    var userInfo: UserInfo = UserInfo()

    var skin: Skin = Skin()

    var adjutantMulti: AdjutantMulti = AdjutantMulti()

    var isAllSkinInjected: Boolean = false

    fun init() {
        session.init()
        isAllSkinInjected = false
    }
}