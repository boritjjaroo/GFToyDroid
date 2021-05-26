package com.github.boritjjaroo.gflib.data

import com.google.gson.JsonObject

class Gun() {

    class GunItem(id: Int, skin: Int) {
        var id: Int = 0
        var skin: Int = 0

        init {
            this.id = id
            this.skin = skin
        }
    }

    private lateinit var guns: MutableMap<Int, GunItem>

    init {
        initSession()
    }

    fun initSession() {
        guns = mutableMapOf<Int, GunItem>()
    }

    fun getGunSkin(id: Int) : Int {
        return guns[id]?.skin ?: 0
    }

    // format
    // gun_id:skin_id,gun_id:skinId, ...
    fun loadPrivateGunSkin(): MutableMap<Int, Int> {
        var map = mutableMapOf<Int, Int>()
        val data = GfData.repository.getGunSkin()
        //GfLog.log.v("gun_skin data loaded : \n$data")
        data?.split(',')?.filter { item -> item.isNotEmpty() }?.forEach { item ->
            val gunSkin = item.split(':')
            if (gunSkin.size == 2) {
                map.put(gunSkin[0].toInt(), gunSkin[1].toInt())
            }
        }
        return map
    }

    fun savePrivateGunSkin(map: Map<Int, Int>) {
        var data = ""
        map.forEach { (gunId, skinId) ->
            data += "$gunId:$skinId,"
        }
        GfData.log.i("private gun_skin data is saved : \n$data")
        GfData.repository.putGunSkin(data)
    }

    fun replaceGunSkinAndSave(gunId: Int, skinId: Int) {
        val map = loadPrivateGunSkin()
        map.put(gunId, skinId)
        savePrivateGunSkin(map)
    }
    fun parseJsonUserInfo(json: JsonObject) {
        val jsonGuns = json.get("gun_with_user_info").asJsonArray
        jsonGuns.forEach { jsonGun ->
            val gunItem = GunItem(jsonGun.asJsonObject["id"].asInt,
                                    jsonGun.asJsonObject["skin"].asInt)
            guns.put(gunItem.id, gunItem)
        }
        GfData.log.i("user_info's ${guns.size} guns data loaded.")
    }
}