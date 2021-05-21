package com.github.boritjjaroo.gftoydroid

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import androidx.preference.PreferenceManager
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData
import com.github.boritjjaroo.gflib.data.GfOptions
import com.github.megatronking.netbare.NetBare
import com.github.megatronking.netbare.NetBareConfig
import com.github.megatronking.netbare.NetBareUtils
import com.github.megatronking.netbare.http.HttpInjectInterceptor
import com.github.megatronking.netbare.http.HttpInterceptorFactory
import com.github.megatronking.netbare.ssl.JKS
import me.weishu.reflection.Reflection

class App : Application(), GfOptions {
    companion object {
        const val JSK_ALIAS = "GFToyDroid"

        private lateinit var sInstance: App

        fun getInstance(): App {
            return sInstance
        }
    }

    private lateinit var mJKS : JKS
    private lateinit var mNetBare : NetBare
    val isVPNStarted: Boolean
        get() {
            return mNetBare.isActive
        }

    override fun onCreate() {
        super.onCreate()

        sInstance = this
        GfData.options = this
        mNetBare = NetBare.get()

        if (mNetBare.isActive) {
            Log.w(GFUtil.TAG, "NetBare is running on app start!!!")
            stopNetBare()
        }

        // 자체 서명 된 인증서 만들기
        mJKS = JKS(this, JSK_ALIAS, JSK_ALIAS.toCharArray(), JSK_ALIAS,JSK_ALIAS,
            JSK_ALIAS, JSK_ALIAS, JSK_ALIAS)

        // NetBare 초기화
        NetBare.get().attachApplication(this, BuildConfig.DEBUG)

        loadAssets()
    }

//    override fun onDestroy() {
//        stopNetBare()
//        mNetBare.unregisterNetBareListener(this)
//    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // On android Q, we can't access Java8EngineWrapper with reflect.
        if (NetBareUtils.isAndroidQ()) {
            Reflection.unseal(base)
        }
    }

    override fun displayDorimitoryBattery(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getBoolean(getString(R.string.key_display_dormitory_battery), false)
    }

    override fun injectAllSkins(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getBoolean(getString(R.string.key_inject_all_skins), false)
    }

    override fun usePrivateAdjutant(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getBoolean(getString(R.string.key_use_private_adjutant), false)
    }

    override fun usePrivateGunSkin(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getBoolean(getString(R.string.key_use_private_gun_skin), false)
    }

    override fun releaseCensorship(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getBoolean(getString(R.string.key_release_censorship), false)
    }

    fun prepareNetBare() {
        // NetBare 서비스 시작
        val configBuilder = NetBareConfig.defaultHttpConfig(mJKS,
            interceptorFactories()).newBuilder()
        configBuilder.addAllowedApplication("kr.txwy.and.snqx")
        configBuilder.excludeSelf(true)
        mNetBare.start(configBuilder.build())
    }

    fun stopNetBare() {
        if (mNetBare.isActive) {
            mNetBare.stop()
        }
    }

    private fun interceptorFactories() : List<HttpInterceptorFactory> {
        val interceptor1 = HttpInjectInterceptor.createFactory(GFPacketInterceptor())
        return listOf(interceptor1)
    }

    private fun loadAssets() {
        val am: AssetManager = resources.assets

        try {
            val input = am.open("skin.json")
            val data = input.readBytes()
            //Log.v(GFUtil.TAG, "json : \n" + GFUtil.byteArrayToUTF8(data))
            GfData.skin.loadSkinData(data)
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}