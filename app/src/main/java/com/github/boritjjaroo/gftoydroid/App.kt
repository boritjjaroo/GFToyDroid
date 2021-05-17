package com.github.boritjjaroo.gftoydroid

import android.app.Application
import android.content.Context
import com.github.megatronking.netbare.NetBare
import com.github.megatronking.netbare.NetBareUtils
import com.github.megatronking.netbare.ssl.JKS
import me.weishu.reflection.Reflection

class App : Application() {
    companion object {
        const val JSK_ALIAS = "GFToyDroid"

        private lateinit var sInstance: App

        fun getInstance(): App {
            return sInstance
        }
    }

    private lateinit var mJKS : JKS

    override fun onCreate() {
        super.onCreate()

        sInstance = this
        // 자체 서명 된 인증서 만들기
        mJKS = JKS(this, JSK_ALIAS, JSK_ALIAS.toCharArray(), JSK_ALIAS,JSK_ALIAS,
            JSK_ALIAS, JSK_ALIAS, JSK_ALIAS)

        // NetBare 초기화
        NetBare.get().attachApplication(this, BuildConfig.DEBUG)
    }

    fun getJSK(): JKS {
        return mJKS
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // On android Q, we can't access Java8EngineWrapper with reflect.
        if (NetBareUtils.isAndroidQ()) {
            Reflection.unseal(base)
        }
    }
}