package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import java.util.*

object Handler {

    var defaultResponseHandler: GfResponsePacket = UnknownResponse()
    var defaultRequestHandler: GfRequestPacket = UnknownRequest()
    var defaultByteDataResponse: GfResponsePacket = ByteDataResponse()

    var requestPackets: Map<String,GfRequestPacket> = mapOf(
        AdjutantRequest.ID to AdjutantRequest(),
        ChangeSkinRequest.ID to ChangeSkinRequest(),
    )

    var responsePackets: Map<String,GfResponsePacket> = mapOf(
        GetUidResponse.ID to GetUidResponse(),
        UserInfoResponse.ID to UserInfoResponse(),
        FriendVisitResponse.ID to FriendVisitResponse(),
    )

    fun sniffResponse(url: String) : Boolean {
        val uri = Uri.parse(url)
        if (uri.host != "gf-game.girlfrontline.co.kr")
            return false
        val path = uri.path
        if (!path!!.startsWith("/index.php"))
            return false
        val (server, pathID) = parseUrl(url)
        return server == "1001"
        //return packets.containsKey(pathID)
    }

    fun handleRequestHeader(url: String) : Int {
        val (server, pathID) = parseUrl(url)
        val packet = this.requestPackets[pathID]
        var contentLength = -1

        if (packet != null)
        {
            contentLength = packet.processHeader()
        }
        else {
            contentLength = this.defaultRequestHandler.processHeader()
        }

        return contentLength
    }

    fun handleRequestBody(url: String, query: ByteArray) : ByteArray? {
        val (server, pathID) = parseUrl(url)
        val packet = this.requestPackets[pathID]
        val modifiedData : ByteArray?

        if (packet != null)
        {
            modifiedData = packet.processBody(query)
        }
        else {
            modifiedData = this.defaultRequestHandler.processBody(query)
        }

        return modifiedData
    }

    fun handleRespose(url: String, packetData: ByteArray) : ByteArray? {
        val (server, pathID) = parseUrl(url)
        val packet = this.responsePackets[pathID]
        val modifiedData : ByteArray?

        if (packet != null)
        {
            modifiedData = packet.process(packetData)
        }
        else if (pathID == "Mission/drawEvent")
        {
            modifiedData = this.defaultByteDataResponse.process(packetData)
        }
        else {
            modifiedData = this.defaultResponseHandler.process(packetData)
        }

        return modifiedData
    }

    fun parseUrl(url: String) : Pair<String, String> {
        val uri = Uri.parse(url)
        val tokenizer = StringTokenizer(uri.path, "/")
        var server = ""
        var pathID = ""
        if (tokenizer.hasMoreTokens()) {
            val index_php = tokenizer.nextToken()
            if (index_php == "index.php" && tokenizer.hasMoreTokens()) {
                server = tokenizer.nextToken()
                if (server == "1001") {
                    while (tokenizer.hasMoreTokens()) {
                        pathID += tokenizer.nextToken()
                        if (tokenizer.hasMoreTokens()) {
                            pathID += "/"
                        }
                    }
                }
            }
        }
        return Pair(server, pathID)
    }
}