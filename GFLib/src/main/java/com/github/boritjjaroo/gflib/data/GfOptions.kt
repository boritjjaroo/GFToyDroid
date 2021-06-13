package com.github.boritjjaroo.gflib.data

interface GfOptions {
    fun displayFriendInfo() : Boolean
    fun injectAllSkins() : Boolean
    fun usePrivateAdjutant() : Boolean
    fun usePrivateGunSkin() : Boolean
    fun injectTargetInfo() : Boolean
    fun releaseCensorship() : Boolean
    fun logUnknownPacketData() : Boolean
}
