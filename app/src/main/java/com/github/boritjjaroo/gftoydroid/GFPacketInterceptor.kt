package com.github.boritjjaroo.gftoydroid

import android.util.Log
import com.github.megatronking.netbare.http.HttpBody
import com.github.megatronking.netbare.http.HttpRequest
import com.github.megatronking.netbare.http.HttpResponse
import com.github.megatronking.netbare.http.HttpResponseHeaderPart
import com.github.megatronking.netbare.injector.InjectorCallback
import com.github.megatronking.netbare.injector.SimpleHttpInjector
import java.io.IOException
import com.github.boritjjaroo.gflib.GFUtil

class GFPacketInterceptor : SimpleHttpInjector() {

    companion object {
        const val TAG = "Packet"
    }

    override fun sniffResponse(response: HttpResponse): Boolean {
        if (response.isHttps) return false
        //if (response.host().canonicalHostName.equals("klanet.duckdns.org")) return false
        //if (response.host().canonicalHostName.equals("gfkrcdn.17996cdn.net")) return false
        //if (response.host().canonicalHostName.startsWith("sn-list")) {
        //    return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.high_quality_illustrations), false) && URL(response.url()).toURI().path.equals("/aNy0jv627jejqDIKgdldAlyQjnr7OExKF5k1daMC80I.txt")
        //}
        return true
    }

    override fun sniffRequest(request: HttpRequest): Boolean {
        if (request.isHttps) return false
        //if (request.host().canonicalHostName.equals("klanet.duckdns.org")) return false
        //if (request.host().canonicalHostName.equals("gfkrcdn.17996cdn.net")) return false
        return true
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
        //this.header = header
        Log.i(TAG, "Response Header : " + header.uri().toString())
        callback.onFinished(header)
    }

    @Throws(IOException::class)
    override fun onResponseInject(httpResponse: HttpResponse, body: HttpBody, callback: InjectorCallback) {
        Log.i(TAG, "onResponseInject(body)")
        callback.onFinished(body)
        Log.i(TAG, "Data Size : " + body.toBuffer().capacity())
        //Log.i(TAG, "Data (UTF8) \n" + GFUtil.byteBufferToUTF8(body.toBuffer()))
        Log.i(TAG, "Data (Hex) \n" + GFUtil.byteBufferToHexString(body.toBuffer(), 256))
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
    }
}