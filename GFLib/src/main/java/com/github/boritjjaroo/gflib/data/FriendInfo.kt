package com.github.boritjjaroo.gflib.data

import java.util.*

class FriendInfo {
    var id: Int = 0
    var dormCount: Int = 0
    var lastVisit: Long = 0

    constructor(id: Int, dormCount: Int, lastVisit: Long) {
        this.id = id
        this.dormCount = dormCount
        this.lastVisit = lastVisit
    }

    constructor(value: String) {
        val values = value.split('|')
        if (values.size == 3) {
            id = values[0].toInt()
            dormCount = values[1].toInt()
            lastVisit = values[2].toLong()
        }
        else {
            id = 0
            dormCount = 0
            lastVisit = 0
        }
    }

    fun getLastVisitString() : String {
        val curTime = Date().time
        val days = (curTime - lastVisit) / 1000 / 3600 / 24
        return "${days}일"
    }
}
