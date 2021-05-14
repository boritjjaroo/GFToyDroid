package com.github.boritjjaroo.gftoydroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.megatronking.netbare.NetBare
import com.github.megatronking.netbare.NetBareConfig
import com.github.megatronking.netbare.NetBareListener
import com.github.megatronking.netbare.http.HttpInjectInterceptor
import com.github.megatronking.netbare.http.HttpInterceptorFactory
import com.github.megatronking.netbare.ssl.JKS
import java.io.IOException

class MainActivity : AppCompatActivity(), View.OnClickListener, NetBareListener {

    companion object {
        private const val REQUEST_CODE_PREPARE = 1
    }

    private lateinit var mNetBare : NetBare
    private lateinit var mActionButton : Button
    private lateinit var mTextView : TextView
    private var mIsVPNStared = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(GFPacketInterceptor.TAG, "onCreate()")

        mNetBare = NetBare.get()
        mActionButton = findViewById(R.id.buttonStartVPN)
        mTextView = findViewById(R.id.textViewMsg)

        mActionButton.setOnClickListener(this)

        if (mNetBare.isActive) {
            stopNetBare()
        }

        mNetBare.registerNetBareListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(GFPacketInterceptor.TAG, "onDestroy()")
        stopNetBare()
        mNetBare.unregisterNetBareListener(this)
    }

    override fun onServiceStarted() {
        Log.i(GFPacketInterceptor.TAG, "onServiceStarted()")
        runOnUiThread {
            mActionButton.setText(R.string.stop_vpn)
        }
    }

    override fun onServiceStopped() {
        Log.i(GFPacketInterceptor.TAG, "onServiceStopped()")
        runOnUiThread {
            mActionButton.setText(R.string.start_vpn)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonStartVPN->{
                Log.i(GFPacketInterceptor.TAG, "onClick()")
                if (mIsVPNStared) {
                    stopNetBare()
                }
                else {
                    prepareNetBare()
                }

            }
        }
    }

    private fun prepareNetBare() {
        Log.i(GFPacketInterceptor.TAG, "prepareNetBare()")

        // 자체 서명 된 인증서 설치
        if (!JKS.isInstalled(this, App.JSK_ALIAS)) {
            try {
                JKS.install(this, App.JSK_ALIAS, App.JSK_ALIAS)
            } catch(e : IOException) {
                // 설치 실패
                Log.w(GFPacketInterceptor.TAG, "Failed to install JKS")
                Log.e(GFPacketInterceptor.TAG, e.toString())
                Log.e(GFPacketInterceptor.TAG, e.stackTraceToString())
            }
            return
        }

        // VPN 구성
        Log.i(GFPacketInterceptor.TAG, "start VPN config")

        val intent = NetBare.get().prepare()
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_PREPARE)
            return
        }
        // NetBare 서비스 시작
        Log.i(GFPacketInterceptor.TAG, "start NetBare")

        var configBuilder = NetBareConfig.defaultHttpConfig(App.getInstance().getJSK(),
            interceptorFactories()).newBuilder()
        configBuilder.addAllowedApplication("kr.txwy.and.snqx")
        configBuilder.excludeSelf(true)
        mNetBare.start(configBuilder.build())

        mIsVPNStared = true
    }

    private fun stopNetBare() {
        Log.i(GFPacketInterceptor.TAG, "stopNetBare()")

        if (mIsVPNStared) {
            mNetBare.stop()
        }
        mIsVPNStared = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PREPARE) {
            prepareNetBare()
        }
    }

    private fun interceptorFactories() : List<HttpInterceptorFactory> {
        val interceptor1 = HttpInjectInterceptor.createFactory(GFPacketInterceptor())
        return listOf(interceptor1)
    }

}