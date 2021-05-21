package com.github.boritjjaroo.gflib.data


object GfData {
    var session: Session = Session()

    lateinit var options: GfOptions
    lateinit var repository: GfDataRepository

    var userInfo: UserInfo = UserInfo()

    var gun: Gun = Gun()

    var skin: Skin = Skin()

    var adjutantMulti: AdjutantMulti = AdjutantMulti()

    var isAllSkinInjected: Boolean = false

    // release censorship for china server
    var hexie: Int = 0

    fun init() {
        session.init()
        gun.init()
        skin.init()
        isAllSkinInjected = false
        hexie = 0
    }
}