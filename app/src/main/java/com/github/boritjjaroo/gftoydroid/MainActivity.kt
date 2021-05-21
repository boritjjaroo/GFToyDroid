package com.github.boritjjaroo.gftoydroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.boritjjaroo.gflib.data.GfData
import com.github.boritjjaroo.gflib.data.GfDataRepository
import com.github.boritjjaroo.gflib.encryption.Sign
import com.github.megatronking.netbare.NetBare
import com.github.megatronking.netbare.NetBareListener
import com.github.megatronking.netbare.ssl.JKS
import com.google.gson.JsonParser
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, NetBareListener, GfDataRepository {

    companion object {
        private const val REQUEST_CODE_PREPARE = 1
    }

    private lateinit var mActionButton : Button
    private lateinit var mTextView : TextView
    private var mBackWait: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getInstance().v("MainActivity::onCreate()")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GfData.repository = this

        mActionButton = findViewById(R.id.buttonStartVPN)
        mTextView = findViewById(R.id.textViewMsg)

        NetBare.get().registerNetBareListener(this)
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(applicationContext, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish() //액티비티 종료
        }
    }

    override fun onDestroy() {
        App.getInstance().v("MainActivity::onDestroy()")
        Toast.makeText(applicationContext, "GFToyDroid is destroyed.", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonStartVPN->{
                if (App.getInstance().isVPNStarted) {
                    App.getInstance().stopNetBare()
                }
                else {
                    prepareNetBare()
                }
            }
            R.id.buttonRefresh->{
                runOnUiThread {
                    updateUI()
                }
            }
            R.id.buttonTest-> {
                test()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSettings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menuAbout -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PREPARE && resultCode == Activity.RESULT_OK) {
            prepareNetBare()
        }
    }

    override fun onServiceStarted() {
        runOnUiThread {
            App.getInstance().i("VPN Service is started.")
            updateUI()
        }
    }

    override fun onServiceStopped() {
        runOnUiThread {
            App.getInstance().i("VPN Service is stopped.")
            updateUI()
        }
    }

    override fun getData(key: String): String? {
        return getPreferences(Context.MODE_PRIVATE).getString(key, null)
    }

    override fun putData(key: String, value: String) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    private fun prepareNetBare() {

        // 자체 서명 된 인증서 설치
        if (!JKS.isInstalled(this, App.JSK_ALIAS)) {
            try {
                JKS.install(this, App.JSK_ALIAS, App.JSK_ALIAS)
            } catch(e : IOException) {
                // 설치 실패
                App.getInstance().w("Failed to install JKS")
                App.getInstance().w(e.toString())
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
        App.getInstance().prepareNetBare()
    }

    private fun updateUI() {
        mActionButton.setText(
            if (App.getInstance().isVPNStarted) R.string.stop_vpn else R.string.start_vpn
        )
        var status = GfData.getStatus()
        status += "VPN Server\n  ${App.getInstance().isVPNStarted}\n\n"
        status += "LastPacketTime\n  ${GFPacketInterceptor.lastInterceptTime}"
        mTextView.text = status
    }

    private fun test() {

        if (true) {
            val date = Date()
            Log.v(App.TAG, "current time : ${date.time.toString()}")
            val date2 = Date(162098608400007)
            Log.v(App.TAG, "time : ${date2.toString()}")
        }
        // Uri Builder test
        if (false) {
            val builder = Uri.Builder()
            builder.path("k1=v1&k2=v2&k3=v3")
            //val uri = builder.build()
            val uri = Uri.parse("k1=v1&k2=v2&k3=v3")
            Log.v(App.TAG, "uri : $uri")
            builder.appendQueryParameter("k2", "v2_new")
            val uri2 = builder.build()
            Log.v(App.TAG, "uri : $uri2")
        }

        // Json test
        if (false) {
            val str1 = """ { "key1":"val1", "key2":"val2" } """
            val json1 = JsonParser.parseString(str1).asJsonObject
            Log.v(App.TAG, "json : $json1")
            json1.addProperty("key1", "val1-mod")
            Log.v(App.TAG, "json : $json1")
            json1.addProperty("key3", "val3")
            Log.v(App.TAG, "json : $json1")
            Log.v(App.TAG, "string cast? : " + json1.get("key1"))
        }

        // Uri test
        if (false) {
            val url = "http://gf-game.girlfrontline.co.kr/index.php/1001/Mall/staticTables"
            val param = "uid=1870807&signcode=ElmNWX75Knzg3TJAkTr%2bp8EFStk2qnzzw5ix2CCsGtsiF%2fv8xNNcZFbM%2fAXSt3b8q95DqggT4tfqCg%3d%3d&req_id=162122475300009"
            //val param2 = "uid=1870807&outdatacode=ElmNWX75Knzg3TVCwWv1%2fZIFSNgzo3%2bvlc%2f7n338X9tMUPjvj4wGOBeX7FPWvSL594VE%2fA0b54C0CFpT%2foz9jBpxZFa23LSSUw6YkQEazsl2fOpjCrel0y%2fW1p4uQtMxwRsDTAtfLhNUIRxK56HPgWK%2bUbDBPPOgBx0R&req_id=162122475300008"
            val uri = Uri.parse(url + "?" + param)
            //val uri2 = Uri.parse(url + "?" + param2)
            Log.v(App.TAG, "param :\n" + param)
            Log.v(App.TAG, "host :\n" + uri.host)
            Log.v(App.TAG, "path :\n" + uri.path)
            Log.v(App.TAG, "pathSegments :\n" + uri.pathSegments.toString())
            Log.v(App.TAG, "query :\n" + uri.query)
            Log.v(App.TAG, "signcode :\n" + uri.getQueryParameter("signcode"))
            Log.v(App.TAG, "outdatacode :\n" + uri.getQueryParameter("outdatacode"))
        }

        // encryption, decryption test code
        if (false) {
            // sign : eb0bae5cf89e8c4fe6a3d6aa8c9e071e
            // original param :
            // uid=1870807&
            // outdatacode=lq3h%2fjZVsyx%2fOUnkHT0VCBx%2bGn1KXmjjCTLD70tSv%2bbqL8Pjn%2fSzB1z7NIu4%2fkeKw6zAjHK4tHqJuycEuGMyXr3PkwkCi7N8nVDGZgdhWCEZCxvHxSFpDAXp50t%2b%2bdqTY6E6luPpBZjISd8BZBBF1rhszeiFOCR9aoFxpkFv8fYlY%2bFPr4jYxNVk2f6ysqQQnwEuR4uHalvS%2b%2bcLI7TJc8tBB3OloKzhRpQe4vwiLx78%2f4k%3d
            // &req_id=162142191200011
            // outdatacode :
            // {"adjutant_multi":"single|0|1|307|5603|0|0,single|1|0|-1|0|0|0,single|2|1|270|5305|1|0,single|3|2|4|12|0|0,combined|0|3|1001|0|0|0,combined|1|0|0|0|0|0"}

            val query = "uid=1870807&outdatacode=lq3h%2fjZVsyx%2fOUnkHT0VCBx%2bGn1KXmjjCTLD70tSv%2bbqL8Pjn%2fSzB1z7NIu4%2fkeKw6zAjHK4tHqJuycEuGMyXr3PkwkCi7N8nVDGZgdhWCEZCxvHxSFpDAXp50t%2b%2bdqTY6E6luPpBZjISd8BZBBF1rhszeiFOCR9aoFxpkFv8fYlY%2bFPr4jYxNVk2f6ysqQQnwEuR4uHalvS%2b%2bcLI7TJc8tBB3OloKzhRpQe4vwiLx78%2f4k%3d&req_id=162142191200011"
            Log.v(App.TAG, "query : \n$query")
            val uri = Uri.parse("http://dummy.host/path?$query")
            val outdatacode = uri.getQueryParameter("outdatacode")
            Log.v(App.TAG, "outdatacode : \n$outdatacode")

            val sign =  Sign("eb0bae5cf89e8c4fe6a3d6aa8c9e071e")
            val jsonStr = String(GfData.session.decryptGFDataRaw(outdatacode!!.toByteArray(), sign))
            Log.v(App.TAG, "json : \n$jsonStr")
            val date = GfData.session.date
            Log.v(App.TAG, "time : " + date.time)

            val newOutdatacode = String(GfData.session.encrpytGFData(jsonStr, false, false, sign, date))
            Log.v(App.TAG, "new Outdatacode : \n$newOutdatacode")
        }

    }

}