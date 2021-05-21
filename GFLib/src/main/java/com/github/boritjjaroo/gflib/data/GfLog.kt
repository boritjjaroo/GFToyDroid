package com.github.boritjjaroo.gflib.data

import android.util.Log

interface GfLog {
//    companion object {
//        val Verbose = Log.VERBOSE // 2
//        val Debug = Log.DEBUG // 3
//        val Info = Log.INFO // 4
//        val Warn = Log.WARN // 5
//        val Error = Log.ERROR // 6
//        val Assert = Log.ASSERT // 7
//    }

    fun v(msg: String) {
        put(Log.VERBOSE, msg)
    }
    fun d(msg: String) {
        put(Log.DEBUG, msg)
    }
    fun i(msg: String) {
        put(Log.INFO, msg)
    }
    fun w(msg: String) {
        put(Log.WARN, msg)
    }
    fun e(msg: String) {
        put(Log.ERROR, msg)
    }
    open fun put(priority: Int, msg: String)
}
