package com.github.boritjjaroo.gflib.packet

import android.net.Uri
import java.util.*

object Handler {

    var defaultResponseHandler: GfPacket = UnknownResponse()
    var defaultRequestHandler: GfPacket = UnknownRequest()
    var defaultByteDataResponse: GfPacket = ByteDataResponse()

    var packets: Map<String,GfPacket> = mapOf(
        GetUid.ID to GetUid(),
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
        val (server, pathID) = parseUriPath(path)
        return server == "1001"
        //return packets.containsKey(pathID)
    }

    fun handleRequest(url: String, params: ByteArray) {
        defaultRequestHandler.process(params)
    }

    fun handleRespose(uriPath: String, packetData: ByteArray) : ByteArray? {
        val (server, pathID) = parseUriPath(uriPath)
        val packet: GfPacket? = this.packets[pathID]
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

    fun parseUriPath(uriPath: String) : Pair<String, String> {
        val tokenizer = StringTokenizer(uriPath, "/")
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