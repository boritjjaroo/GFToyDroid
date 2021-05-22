package com.github.boritjjaroo.gftoydroid

import com.github.boritjjaroo.gflib.data.GfLog
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
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class GFPacketInterceptor(logger: GfLog) : SimpleHttpInjector() {

    companion object {
        var lastInterceptTime = Date(0)
    }

    private var log: GfLog
    private var responseHeader: HttpResponseHeaderPart? = null
    private var responseBuffer: ByteArrayOutputStream? = null

    init {
        this.log = logger
    }

    override fun sniffRequest(request: HttpRequest): Boolean {
        lastInterceptTime = Date()
        log.v("Interceptor::sniffRequest() : " + request.url())
        return true
    }

    override fun sniffResponse(response: HttpResponse): Boolean {
        lastInterceptTime = Date()
        log.v("Interceptor::sniffResponse() : " + response.url())
        return Handler.sniffResponse(response.url())
    }

    @Throws(IOException::class)
    override fun onRequestInject(header: HttpRequestHeaderPart, callback: InjectorCallback) {
        val result = Handler.handleRequestHeader(header.uri().toString())
        if (0 < result) {
            val builder = header.newBuilder()
            builder.removeHeader("Content-Length")
            builder.addHeader("Transfer-Encoding", "chunked")
            val newHeader = builder.build()
            callback.onFinished(newHeader)
        }
        else {
            callback.onFinished(header)
        }
    }

    @Throws(IOException::class)
    override fun onRequestInject(request: HttpRequest, body: HttpBody, callback: InjectorCallback) {
        log.v("Interceptor::onRequestInject(body) : " + request.url())
        val headers = request.requestHeader("Transfer-Encoding")
        val chunked = headers != null && headers.size == 1 && headers[0].equals("chunked")
        var modifiedBody = Handler.handleRequestBody(request.url(), body.toBuffer().array())

        if (chunked && modifiedBody == null)
            modifiedBody = body.toBuffer().array()

        if (chunked) {
            val outputStream = ByteArrayOutputStream()
            val chunkedOutputStream = ChunkedOutputStream(outputStream)
            chunkedOutputStream.write(modifiedBody)
            chunkedOutputStream.finish()
            val chunkedBody = outputStream.toByteArray()
            outputStream.close()
            callback.onFinished(BufferStream(ByteBuffer.wrap(chunkedBody)))
        }
        else {
            callback.onFinished(body)
        }
    }

    @Throws(IOException::class)
    override fun onResponseInject(header: HttpResponseHeaderPart, callback: InjectorCallback) {
        log.v("onResponseInject(header) : " + header.uri().toString())
        this.responseHeader = header
        this.responseBuffer = ByteArrayOutputStream()
        callback.onFinished(header)
    }

    @Throws(IOException::class)
    override fun onResponseInject(httpResponse: HttpResponse, body: HttpBody, callback: InjectorCallback) {
        log.v("onResponseInject(body)")

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

                log.v("Data Size : " + responseByteArray.size)
                val url = this.responseHeader!!.uri().toString()

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
                log.e(e.toString())
            }

            // pass the original chunked data that is collected fully
            callback.onFinished(BufferStream(ByteBuffer.wrap(callbackByteArray)))
        }
    }
}