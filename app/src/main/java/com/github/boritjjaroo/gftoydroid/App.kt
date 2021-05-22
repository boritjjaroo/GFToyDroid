package com.github.boritjjaroo.gftoydroid

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import androidx.preference.PreferenceManager
import com.github.boritjjaroo.gflib.data.GfData
import com.github.boritjjaroo.gflib.data.GfOptions
import com.github.megatronking.netbare.NetBare
import com.github.megatronking.netbare.NetBareUtils
import com.github.megatronking.netbare.ssl.JKS
import me.weishu.reflection.Reflection

class App : Application(), GfOptions {
    companion object {
        const val JSK_ALIAS = "GFToyDroid"
         lateinit var mJKS : JKS

        private lateinit var sInstance: App

        fun getInstance(): App {
            return sInstance
        }
    }

    override fun onCreate() {
        super.onCreate()

        sInstance = this
        GfData.options = this

        // 자체 서명 된 인증서 만들기
        mJKS = JKS(this, JSK_ALIAS, JSK_ALIAS.toCharArray(), JSK_ALIAS,JSK_ALIAS,
            JSK_ALIAS, JSK_ALIAS, JSK_ALIAS)

        // NetBare 초기화
        NetBare.get().attachApplication(this, BuildConfig.DEBUG)

        loadAssets()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // On android Q, we can't access Java8EngineWrapper with reflect.
        if (NetBareUtils.isAndroidQ()) {
            Reflection.unseal(base)
        }
    }

    override fun displayDormitoryBattery(): Boolean {
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

    override fun logUnknownPacketData(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getBoolean(getString(R.string.key_log_unknown_packet_data), false)
    }

    private fun loadAssets() {
        val am: AssetManager = resources.assets

        try {
            val input = am.open("skin.json")
            val data = input.readBytes()
            GfData.skin.loadSkinData(data)
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}