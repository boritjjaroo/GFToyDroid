package com.github.boritjjaroo.gftoydroid

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.packet.Handler
import com.github.megatronking.netbare.http.HttpBody
import com.github.megatronking.netbare.http.HttpRequest
import com.github.megatronking.netbare.http.HttpResponse
import com.github.megatronking.netbare.http.HttpResponseHeaderPart
import com.github.megatronking.netbare.injector.InjectorCallback
import com.github.megatronking.netbare.injector.SimpleHttpInjector
import com.github.megatronking.netbare.stream.BufferStream
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
    }

    private var responseHeader: HttpResponseHeaderPart? = null
    private var responseBuffer: ByteArrayOutputStream? = null


    override fun sniffRequest(request: HttpRequest): Boolean {
        Log.i(GFUtil.TAG, "Interceptor::sniffRequest() : " + request.url())
        return true
    }

    override fun sniffResponse(response: HttpResponse): Boolean {
        Log.i(GFUtil.TAG, "Interceptor::sniffResponse() : " + response.url())
        return Handler.sniffResponse(response.url())
    }

    @Throws(IOException::class)
    override fun onRequestInject(request: HttpRequest, body: HttpBody, callback: InjectorCallback) {
        Log.i(GFUtil.TAG, "Interceptor::onRequestInject(body) : " + request.url())
        Handler.handleRequest(request.url(), body.toBuffer().array())
        callback.onFinished(body)
    }

    @Throws(IOException::class)
    override fun onResponseInject(header: HttpResponseHeaderPart, callback: InjectorCallback) {
        Log.i(GFUtil.TAG, "onResponseInject(header) : " + header.uri().toString())
        this.responseHeader = header
        this.responseBuffer = ByteArrayOutputStream()
        callback.onFinished(header)
    }

    @Throws(IOException::class)
    override fun onResponseInject(httpResponse: HttpResponse, body: HttpBody, callback: InjectorCallback) {
        Log.i(GFUtil.TAG, "onResponseInject(body)")

        var isResponseBufferReady = false
        var isChunked = false

        val bodyByteArray = body.toBuffer().array()
        this.responseBuffer!!.write(bodyByteArray)

        if ("chunked" == this.responseHeader!!.header("Transfer-Encoding")) {
            isChunked = true
            if (String(bodyByteArray).endsWith("0\r\n\r\n")) {
                isResponseBufferReady = true
            }
        }
        else {
            isResponseBufferReady = true
        }

        if (isResponseBufferReady) {

            val isGzip = "gzip" == this.responseHeader!!.header("Content-Encoding")
            this.responseBuffer!!.flush()

            try {
                val byteArrayInputStream = ByteArrayInputStream(this.responseBuffer!!.toByteArray())
                var inputStream: InputStream = byteArrayInputStream
                if (isChunked) {
                    inputStream = ChunkedInputStream(inputStream)
                }
                if (isGzip) {
                    inputStream = GZIPInputStream(inputStream)
                }

                val responseByteArray = IOUtils.toByteArray(inputStream)
                inputStream.close()

                Log.i(GFUtil.TAG, "Data Size : " + responseByteArray.size)
                val uriPath = this.responseHeader!!.uri().path ?: ""

                Handler.handleRespose(uriPath, responseByteArray)

            } catch (e : Exception) {
                Log.e(GFUtil.TAG, e.toString())
                //Log.e(GFUtil.TAG, e.stackTraceToString())
            }

            // pass the original chunked data that is collected fully
            callback.onFinished(BufferStream(ByteBuffer.wrap(this.responseBuffer!!.toByteArray())))
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
    }
}