package com.github.boritjjaroo.gflib.data

import android.util.Log

interface GfLog {
    companion object {
//        val Verbose = Log.VERBOSE // 2
//        val Debug = Log.DEBUG // 3
//        val Info = Log.INFO // 4
//        val Warn = Log.WARN // 5
//        val Error = Log.ERROR // 6
//        val Assert = Log.ASSERT // 7
        const val TOAST = 0x10000
        const val FORCE = 0x20000
    }

    fun v(msg: String, priorityEx: Int = 0) {
        put(Log.VERBOSE or priorityEx, msg)
    }
    fun d(msg: String, priorityEx: Int = 0) {
        put(Log.DEBUG or priorityEx, msg)
    }
    fun i(msg: String, priorityEx: Int = 0) {
        put(Log.INFO or priorityEx, msg)
    }
    fun w(msg: String, priorityEx: Int = 0) {
        put(Log.WARN or priorityEx, msg)
    }
    fun e(msg: String, priorityEx: Int = 0) {
        put(Log.ERROR or priorityEx, msg)
    }
    open fun put(priorityEx: Int, msg: String)
}
