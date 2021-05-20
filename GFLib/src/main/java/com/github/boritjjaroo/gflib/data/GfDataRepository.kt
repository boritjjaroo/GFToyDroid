package com.github.boritjjaroo.gflib.data

interface GfDataRepository {
    fun getData(key: String) : String?
    fun putData(key: String, value: String)

    class Key{
        companion object {
            val AdjutantMulti = "AdjutantMulti"
        }
    }

    fun getAdjutantMulti() : String? {
        return getData(Key.AdjutantMulti)
    }

    fun putAdjutantMulti(value: String) {
        putData(Key.AdjutantMulti, value)
    }
}
