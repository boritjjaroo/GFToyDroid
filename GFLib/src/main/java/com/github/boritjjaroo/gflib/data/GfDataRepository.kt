package com.github.boritjjaroo.gflib.data

interface GfDataRepository {
    fun getData(key: String) : String?
    fun putData(key: String, value: String)

    class Key{
        companion object {
            const val AdjutantMulti = "AdjutantMulti"
            const val GunSkin = "GunSkin"
            const val Friends = "Friends"
        }
    }

    fun getAdjutantMulti() : String? {
        return getData(Key.AdjutantMulti)
    }

    fun putAdjutantMulti(value: String) {
        putData(Key.AdjutantMulti, value)
    }

    fun getGunSkin() : String? {
        return getData(Key.GunSkin)
    }

    fun putGunSkin(value: String) {
        putData(Key.GunSkin, value)
    }

    fun getFriends() : String? {
        return getData(Key.Friends)
    }

    fun putFriends(value: String) {
        putData(Key.Friends, value)
    }
}
