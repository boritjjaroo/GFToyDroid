package com.github.boritjjaroo.gftoydroid

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.data.GfData
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

        mNetBare = NetBare.get()
        mActionButton = findViewById(R.id.buttonStartVPN)
        mTextView = findViewById(R.id.textViewMsg)

        //mActionButton.setOnClickListener(this)

        if (mNetBare.isActive) {
            stopNetBare()
        }

        mNetBare.registerNetBareListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopNetBare()
        mNetBare.unregisterNetBareListener(this)
    }

    override fun onServiceStarted() {
        runOnUiThread {
            mActionButton.setText(R.string.stop_vpn)
        }
    }

    override fun onServiceStopped() {
        runOnUiThread {
            mActionButton.setText(R.string.start_vpn)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonStartVPN->{
                if (mIsVPNStared) {
                    stopNetBare()
                }
                else {
                    prepareNetBare()
                }

            }
            R.id.buttonTest-> {
                mTextView.setText("Test")
                test()
            }
        }
    }

    private fun prepareNetBare() {

        // 자체 서명 된 인증서 설치
        if (!JKS.isInstalled(this, App.JSK_ALIAS)) {
            try {
                JKS.install(this, App.JSK_ALIAS, App.JSK_ALIAS)
            } catch(e : IOException) {
                // 설치 실패
                Log.w(GFUtil.TAG, "Failed to install JKS")
                Log.e(GFUtil.TAG, e.toString())
                Log.e(GFUtil.TAG, e.stackTraceToString())
            }
            return
        }

        // VPN 구성
        val intent = NetBare.get().prepare()
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_PREPARE)
            return
        }
        // NetBare 서비스 시작
        val configBuilder = NetBareConfig.defaultHttpConfig(App.getInstance().getJSK(),
            interceptorFactories()).newBuilder()
        configBuilder.addAllowedApplication("kr.txwy.and.snqx")
        configBuilder.excludeSelf(true)
        mNetBare.start(configBuilder.build())

        mIsVPNStared = true
    }

    private fun stopNetBare() {
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

    private fun test() {

        // Uri test
        if (true) {
            val url = "http://gf-game.girlfrontline.co.kr/index.php/1001/Mall/staticTables"
            val param = "uid=1870807&signcode=ElmNWX75Knzg3TJAkTr%2bp8EFStk2qnzzw5ix2CCsGtsiF%2fv8xNNcZFbM%2fAXSt3b8q95DqggT4tfqCg%3d%3d&req_id=162122475300009"
            val param2 = "uid=1870807&outdatacode=ElmNWX75Knzg3TVCwWv1%2fZIFSNgzo3%2bvlc%2f7n338X9tMUPjvj4wGOBeX7FPWvSL594VE%2fA0b54C0CFpT%2foz9jBpxZFa23LSSUw6YkQEazsl2fOpjCrel0y%2fW1p4uQtMxwRsDTAtfLhNUIRxK56HPgWK%2bUbDBPPOgBx0R&req_id=162122475300008"
            val uri = Uri.parse(url + "?" + param)
            val uri2 = Uri.parse(url + "?" + param2)
            Log.i(GFUtil.TAG, "param :\n" + param)
            Log.i(GFUtil.TAG, "host :\n" + uri.host)
            Log.i(GFUtil.TAG, "path :\n" + uri.path)
            Log.i(GFUtil.TAG, "pathSegments :\n" + uri.pathSegments.toString())
            Log.i(GFUtil.TAG, "query :\n" + uri.query)
            Log.i(GFUtil.TAG, "signcode :\n" + uri.getQueryParameter("signcode"))
            Log.i(GFUtil.TAG, "outdatacode :\n" + uri.getQueryParameter("outdatacode"))
        }

        // encryption, decryption test code
        if (false) {
            // 최초 sign 성공
            val bufferStr = "#fmUM08rmbj9RCa/yNpsJn/3Gs2KoT+zDb2nnarYZD3QtVc7PpuwYOt+gcXrcf3ywhmPxQ3cYQbKE+jkZoWwai58Ed57NQsdijsWgJscU5ggKShO3PMrV5R3qAPSSQM+ff8zM6hmwodmvsQm9O5ZYHqqOBh7zTAgxoblid5aErwdYrVHmK386fYtGqFB6nbkPtChGGztXdQCKoOT5M7eG/zH3BvyxxxK3lrcIbFjxVqAr2flPEG3WlDibfNH4xaNu+axIITUIchx46I/u9aW+X3TS3no+W1PM"
            Log.i(GFUtil.TAG, "data : count " + bufferStr.length + "\n" + bufferStr)
            val buffer: ByteArray = bufferStr.toByteArray()
            val data = GfData.session.decryptGFData(buffer)
            Log.i(GFUtil.TAG, "json :\n" + data.toString())

            GfData.session.sign = "c5eabaa63219cf9fa2651756c88f9fa2"
            // 성공한
            val bufferStr2 = "#SKBHAK7kjhV8buC6pU0WQ56xgyeRkejtfXsAL+eG4Mfua/3nB+DP2y1b1T9fsJ4HEqbg3fTw/AI7nuRe5q/OLVrz3ibdpY/j3XlKXjJdmjpvO0Cgzs/qrjPhUk9LXdFagT6iSDgsxIu/p061VLN+ZBpqwBS59d1bZhadsGth8Ow9CqR1+qwzmGEIj/j6EbcUERwTD1tuyBxpfRZxgVtTpr1bD4t0lQAmkSyao/dUJbi8lExcfGoeQGvEpk3N8l4PEK4yhbv3pxA/yGGQHvgukyBS7Yuu7jIC8pUj5rUaLosEOsZT7z81mdbtluJB+k9jic/u/Bmhn1VGJbU4yD4yWhO7Vp8RInMaumq+vNZlK2vqXn8MBXMkZIr8M3RFEHTJoaclr5iq+1hid9dUIK6l5qgrbGM7hYqwiHbBM1Plkg/rotvdtUZWTggUk+ABnPlSQ3i2rATce89R9Kwf84jhWMxOht7lJ0bCLSPo34NiQX/ig/yPdSiTm/d6qY6soOIG03VvwPcUpBQ4JTNKVYjx6FU/CpHC9wNcSNCTZ9RZsr38xna2BlqQ3IQb387UmscyaeiTLavgAawMdIPGXpagNgJ7JHFctkJot4qPzVap5gWafmvzha7e0+1bcXKNKIfOEPJ/0aeTMVyLlTVrhYEnbbuT5WiTm6fyhaj1yHJykcWLSJfQswauWrJ63JaM72YVLorp5LHwfFFLKpvnID8/nqDu+gBrEHLbi7dzVq7j8DNnOtAzMwr4yA1ew8mX61DQJchMntT3Z/muoYT7gKVY54CYT84FctS5NZzZC9dG09B10UZ5/b2r+SplkHONtbDHqEfEHSfzYHAjGhgWEBqB3k05d9aHl1azBpQvLo/lQInqCAFDtDsc07dNx/PyFI3NAkUGZKEo3t6FDOJ9zNoaE3OD25AKbssgf9NvdMVmt7+I6BNsRTePjsb4I+mKT847Jmbfw2rhWu/kP/46C1SuvydAU6DLU1aywX33f3chgOCAdBPQqTJr9eQB7EQ6iBUTJYnB/MiUZE0hIGbpAtYJnjBXkOtNHWoCZGwtSKn51fiMS3JHHey5fKjB2EnyQjR0+9PzBd5zDo6tSsQEKZE5XLby9qtjTbrwMXzWrUbHqFLslQpl0LMDDiBhFDrPMeYEk2H5lz1BMCapSqI6HzGZsyubivA26VDj2QhANVBb3EiufSZWnlqNHoBem1pY3hhNg/UAYfJeQlKqAMyhuR5thyUVnVqcYkmfHzBi5TWaT+oopp5uyQHvxXaZHUxBwk+zuIqy3jq3z9X0i/wJSB/duquQwMLpncg9x7LmfSNyXmn+iimZUHc49LICbGp/es8CGdZ1qndWFBiTBgVWmnW4YlLkPvFQ"
            Log.i(GFUtil.TAG, "data : count " + bufferStr2.length + "\n" + bufferStr2)
            val buffer2: ByteArray = bufferStr2.toByteArray()
            val data2 = GfData.session.decryptGFData(buffer2)
            Log.i(GFUtil.TAG, "json :\n" + data2.toString())

            // 실패한
            val bufferStr3 = "#SKBHAK7kjhV8b7Tq8xwQQJS0gCaRzu/oIywAL+eG4Mfua/3nH+Rdww0j1fxPdy/hVBaQpd+r99yLCRM3n/N75tIHylDMpnuwmoRsCOyTZWEm7Suf2CK2hJYDYr4UI0odD3p6Pvm8CEk5hPcQYtNY35ZzA6BoL/9c1qV3OKg/Sq0nGc7+CZglwR0n2nXy0qxYtROymoB3LYH+ukPB6n2xuUVFdt8vPlQc6NCiI/bH0NSUaPNPC17QnJwBKcZuVH4m6y1fjHxdzhYncuyli04lm08HCpkICfk/sI06EQVlY1lm3yfoKuVhjhWqi1JKX1qW+Gt7P1z6tpDOSykHckwbisV7k1FZ0oIMHzQZlnk="
            Log.i(GFUtil.TAG, "data : count " + bufferStr3.length + "\n" + bufferStr3)
            val buffer3: ByteArray = bufferStr3.toByteArray()
            val data3 = GfData.session.decryptGFData(buffer3)
            Log.i(GFUtil.TAG, "json :\n" + data3.toString())

//            var byteArray = GfData.session.encrpytGFData(data, true)
//            Log.i(
//                GFUtil.TAG,
//                "Hex : \n" + GFUtil.byteArrayToHexString(byteArray, 1024)
//            )
//
//            var byteBuffer = ByteBuffer.wrap(byteArray)
//            var encryptStr = GFUtil.byteBufferToUTF8(byteBuffer)
//            Log.i(GFUtil.TAG, "Org : \n" + bufferStr)
//            Log.i(GFUtil.TAG, "Enc : \n" + encryptStr)
        }

    }

}