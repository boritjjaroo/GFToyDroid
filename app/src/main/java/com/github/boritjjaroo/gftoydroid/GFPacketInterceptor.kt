package com.github.boritjjaroo.gftoydroid

import android.util.Log
import com.github.boritjjaroo.gflib.GFUtil
import com.github.boritjjaroo.gflib.packet.Handler
import com.github.megatronking.netbare.http.*
import com.github.megatronking.netbare.injector.InjectorCallback
import com.github.megatronking.netbare.injector.SimpleHttpInjector
import com.github.megatronking.netbare.stream.BufferStream
import org.apache.commons.httpclient.ChunkedInputStream
import org.apache.commons.httpclient.ChunkedOutputStream
import org.apache.commons.io.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class GFPacketInterceptor : SimpleHttpInjector() {

    companion object {
    }

    private var responseHeader: HttpResponseHeaderPart? = null
    private var responseBuffer: ByteArrayOutputStream? = null


    override fun sniffRequest(request: HttpRequest): Boolean {
        Log.v(GFUtil.TAG, "Interceptor::sniffRequest() : " + request.url())
        return true
    }

    override fun sniffResponse(response: HttpResponse): Boolean {
        Log.v(GFUtil.TAG, "Interceptor::sniffResponse() : " + response.url())
        return Handler.sniffResponse(response.url())
    }

    @Throws(IOException::class)
    override fun onRequestInject(header: HttpRequestHeaderPart, callback: InjectorCallback) {
        val contentLength = Handler.handleRequestHeader(header.uri().toString())
        if (0 <= contentLength) {
            val newHeader = header.newBuilder().replaceHeader("Content-Length", contentLength.toString()).build()
            callback.onFinished(newHeader)
        }
        else {
            callback.onFinished(header)
        }
    }

    @Throws(IOException::class)
    override fun onRequestInject(request: HttpRequest, body: HttpBody, callback: InjectorCallback) {
        Log.v(GFUtil.TAG, "Interceptor::onRequestInject(body) : " + request.url())
        val modifiedBody = Handler.handleRequestBody(request.url(), body.toBuffer().array())
        if (modifiedBody != null) {
            callback.onFinished(BufferStream(ByteBuffer.wrap(modifiedBody)))
        }
        else {
            callback.onFinished(body)
        }
    }

    @Throws(IOException::class)
    override fun onResponseInject(header: HttpResponseHeaderPart, callback: InjectorCallback) {
        Log.v(GFUtil.TAG, "onResponseInject(header) : " + header.uri().toString())
        this.responseHeader = header
        this.responseBuffer = ByteArrayOutputStream()
        callback.onFinished(header)
    }

    @Throws(IOException::class)
    override fun onResponseInject(httpResponse: HttpResponse, body: HttpBody, callback: InjectorCallback) {
        Log.v(GFUtil.TAG, "onResponseInject(body)")

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
            var callbackByteArray = this.responseBuffer!!.toByteArray()

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

                Log.v(GFUtil.TAG, "Data Size : " + responseByteArray.size)
                val url = this.responseHeader!!.uri().toString() ?: ""

                val modifiedByteArray = Handler.handleRespose(url, responseByteArray)

                if (modifiedByteArray != null) {
                    val outputStream = ByteArrayOutputStream()
                    val chunkedOutputStream = ChunkedOutputStream(outputStream)
                    val gzipOutputStream = GZIPOutputStream(chunkedOutputStream)
                    gzipOutputStream.write(modifiedByteArray)
                    gzipOutputStream.finish()
                    chunkedOutputStream.finish()
                    callbackByteArray = outputStream.toByteArray()
                    gzipOutputStream.close()
                }

            } catch (e : Exception) {
                Log.e(GFUtil.TAG, e.toString())
                //Log.e(GFUtil.TAG, e.stackTraceToString())
            }

            // pass the original chunked data that is collected fully
            callback.onFinished(BufferStream(ByteBuffer.wrap(callbackByteArray)))
        }

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
//                }
    }
}