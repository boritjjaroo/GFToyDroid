package com.github.boritjjaroo.gftoydroid

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.megatronking.netbare.http.HttpBody
import com.github.megatronking.netbare.http.HttpRequest
import com.github.megatronking.netbare.http.HttpResponse
import com.github.megatronking.netbare.http.HttpResponseHeaderPart
import com.github.megatronking.netbare.injector.InjectorCallback
import com.github.megatronking.netbare.injector.SimpleHttpInjector
import org.apache.commons.httpclient.ChunkedInputStream
import org.apache.commons.io.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.zip.GZIPInputStream

class GFPacketInterceptor : SimpleHttpInjector() {

    companion object {
        const val TAG = "Packet"
    }

    private var responseHeader: HttpResponseHeaderPart? = null
    private var responseBuffer: ByteArrayOutputStream? = null


    override fun sniffRequest(request: HttpRequest): Boolean {
        //if (request.host().canonicalHostName.equals("klanet.duckdns.org")) return false
        //if (request.host().canonicalHostName.equals("gfkrcdn.17996cdn.net")) return false
        return true
    }

    override fun sniffResponse(response: HttpResponse): Boolean {
        if (response.path().startsWith("/index.php"))
            return true
        //if (response.host().canonicalHostName.equals("klanet.duckdns.org")) return false
        //if (response.host().canonicalHostName.equals("gfkrcdn.17996cdn.net")) return false
        //if (response.host().canonicalHostName.startsWith("sn-list")) {
        //    return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.high_quality_illustrations), false) && URL(response.url()).toURI().path.equals("/aNy0jv627jejqDIKgdldAlyQjnr7OExKF5k1daMC80I.txt")
        //}
        return false
    }

    @Throws(IOException::class)
    override fun onRequestInject(request: HttpRequest, body: HttpBody, callback: InjectorCallback) {
        Log.i(TAG, "onRequestInject(body)")
        //this.request = RequestFactory[request.path(), body.toBuffer().array()]
        Log.i(TAG, "Request : " + request.url().toString())
        Log.i(TAG, "Data Size : " + body.toBuffer().capacity())
        //Log.i(TAG, "Data (UTF8) \n" + GFUtil.byteBufferToUTF8(body.toBuffer()))
        //Log.i(TAG, "Data (Hex) \n" + GFUtil.byteBufferToHexString(body.toBuffer(), 256))

        callback.onFinished(body)
    }

    @Throws(IOException::class)
    override fun onResponseInject(header: HttpResponseHeaderPart, callback: InjectorCallback) {
        Log.i(TAG, "onResponseInject(header)")
        this.responseHeader = header
        responseBuffer = ByteArrayOutputStream()
        Log.i(TAG, "Response Header : " + header.uri().toString())
        callback.onFinished(header)
    }

    @Throws(IOException::class)
    override fun onResponseInject(httpResponse: HttpResponse, body: HttpBody, callback: InjectorCallback) {
        Log.i(TAG, "onResponseInject(body)")

        var isResponseBufferReady = false
        var isChunked = false
        var bodyByteArray = body.toBuffer().array()
        responseBuffer!!.write(bodyByteArray)

        if ("chunked" == responseHeader!!.header("Transfer-Encoding")) {
            isChunked = true
            if (String(bodyByteArray).endsWith("0\r\n\r\n")) {
                isResponseBufferReady = true
            }
        }
        else {
            isResponseBufferReady = true
        }

        if (isResponseBufferReady) {
            try {
                var byteArrayInputStream = ByteArrayInputStream(responseBuffer!!.toByteArray())
                var inputStream: InputStream? = null
                if ("gzip" == responseHeader!!.header("Content-Encoding")) {
                    if (isChunked) {
                        inputStream = GZIPInputStream(ChunkedInputStream(byteArrayInputStream))
                    }
                    else {
                        inputStream = GZIPInputStream(byteArrayInputStream)
                    }
                }
                else {
                    if (isChunked) {
                        inputStream = ChunkedInputStream(byteArrayInputStream)
                    }
                    else {
                        inputStream = byteArrayInputStream
                    }
                }

                val reponseByteArray = IOUtils.toByteArray(inputStream)
                inputStream.close()

                Log.i(TAG, "Data Size : " + reponseByteArray.size)
                var byteBuffer = ByteBuffer.allocate(reponseByteArray.size)
                byteBuffer.put(reponseByteArray)
                byteBuffer.rewind()
                Log.i(TAG, "Data (UTF8) \n" + GFUtil.byteBufferToUTF8(byteBuffer))
                Log.i(TAG, "Data (Hex) \n" + GFUtil.byteArrayToHexString(reponseByteArray, 1024))

            } catch (e : Exception) {

            }
        }

//        if ("chunked" != header!!.header("Transfer-Encoding") || "gzip" != header!!.header("Content-Encoding")) {
//            Log.d("INDEX", String(body.toBuffer().array()))
//            callback.onFinished(body)
//            return
//        }
//
//        if (header!!.uri().path == "/index.php") {
//            //server has something wrong
//            callback.onFinished(body)
//            return
//        }
//
//        val bytes = body.toBuffer().array()
//        buffer!!.write(bytes)
//        if (String(bytes).endsWith("\r\n\r\n")) {
//            try {
//                val inputStream = GZIPInputStream(
//                    ChunkedInputStream(
//                        ByteArrayInputStream(buffer!!.toByteArray())
//                    )
//                )
//                val line = IOUtils.toByteArray(inputStream)
//                inputStream.close()
//                val uri = header!!.uri().path
//                val newResponse: ByteArray
//                if (uri!!.startsWith(session.uriHeader)) {
//                    val response = ResponseFactory[
//                            uri.substring(Integer.min(uri.length, session.uriHeader.length)),
//                            line,
//                            request!!
//                    ]
//
//                    try {
//                        session.networkManager.responseHandlerManager.handle(response)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//
//                    if (response.isEdited) {
//                        val outputStream = ByteArrayOutputStream()
//                        val chunkedOutputStream = ChunkedOutputStream(outputStream)
//                        val gzipOutputStream = GZIPOutputStream(chunkedOutputStream)
//                        gzipOutputStream.write(response.buffer!!)
//                        gzipOutputStream.finish()
//                        chunkedOutputStream.finish()
//                        newResponse = outputStream.toByteArray()
//                        gzipOutputStream.close()
//                    } else {
//                        newResponse = buffer!!.toByteArray()
//                    }
//                } else if(httpResponse.host().hostName.startsWith("sn-list") && header!!.uri().path.equals("/aNy0jv627jejqDIKgdldAlyQjnr7OExKF5k1daMC80I.txt")) {
//                    try {
//                        val outputStream = ByteArrayOutputStream()
//                        val chunkedOutputStream = ChunkedOutputStream(outputStream)
//                        val gzipOutputStream = GZIPOutputStream(chunkedOutputStream)
//                        val file = FileInputStream(context.dataDir.absolutePath + "/update.bin")
//                        gzipOutputStream.write(file.readBytes())
//                        gzipOutputStream.finish()
//                        chunkedOutputStream.finish()
//                        newResponse = outputStream.toByteArray()
//                        gzipOutputStream.close()
//                    } catch (e: Throwable) {
//                        e.printStackTrace()
//                        return
//                    }
//
//                } else {
//                    newResponse = buffer!!.toByteArray()
//                }
//                buffer!!.close()
//                (context.applicationContext as PrototypeG).lastResponseTime = Instant.now().plusSeconds(60 * 5)
//                callback.onFinished(BufferStream(ByteBuffer.wrap(newResponse)))
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }
        callback.onFinished(body)
    }
}