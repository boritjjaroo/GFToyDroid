package com.github.boritjjaroo.gflib.data

import java.util.*

class Friends {

    private var map: MutableMap<Int, FriendInfo> = mutableMapOf()
    lateinit private var newMap: MutableMap<Int, FriendInfo>

    fun init() {
        loadSaveData()
    }

    fun initSession() {
    }

    fun get(id: Int) : FriendInfo? {
        return map[id]
    }

    fun beginUpdate() {
        newMap = mutableMapOf()
    }

    fun update(id: Int) {
        newMap.put(id, map[id] ?: FriendInfo(id, 0, 0))
    }

    fun endUpdate() {
        map = newMap
        saveData()
    }

    fun updateDormCount(id: Int, count: Int) {
        if (map.containsKey(id)) {
            map[id]!!.dormCount = count
            saveData()
        }
    }

    fun beginUpdateLastVisit() {
    }

    fun updateLastVisit(id: Int) {
        if (map.containsKey(id)) {
            map[id]!!.lastVisit = Date().time
        }
    }

    fun endUpdateLastVisit() {
        saveData()
    }

    fun loadSaveData() {
        map.clear()
        val value = GfData.repository.getFriends()
        value?.split(',')?.forEach { data ->
            val friend = FriendInfo(data)
            map.put(friend.id, friend)
        }
        GfData.log.v("${map.size} friend infos loaded.")
    }

    fun saveData() {
        var str = ""
        map.forEach { (_, friend) ->
            str += "${friend.id}|${friend.dormCount}|${friend.lastVisit},"
        }
        GfData.repository.putFriends(str.dropLast(1))
        GfData.log.v("${map.size} friend infos saved.")
    }
}