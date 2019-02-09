package com.seigneur.gauvain.needforstream.data.rxWebSocket

import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

sealed class RxWebSocket(val webSocket: WebSocket?){
    class BinaryMessage(webSocket: WebSocket?, val bytes: ByteString?) : RxWebSocket(webSocket)
    class Opened(webSocket: WebSocket?, val response: Response?) : RxWebSocket(webSocket)
    class StringMessage(webSocket: WebSocket?, val text: String?) : RxWebSocket(webSocket)
    class Failure(webSocket: WebSocket?, val t: Throwable?, val response: Response?) : RxWebSocket(webSocket)
    class Closed(webSocket: WebSocket?, val code: Int, val reason: String?) : RxWebSocket(webSocket)
    class Closing(webSocket: WebSocket?, val code: Int, val reason: String?) : RxWebSocket(webSocket)
}