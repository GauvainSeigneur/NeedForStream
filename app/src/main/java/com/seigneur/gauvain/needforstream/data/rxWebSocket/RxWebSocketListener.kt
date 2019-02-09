package com.seigneur.gauvain.needforstream.data.rxWebSocket

import io.reactivex.FlowableEmitter
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class RxWebSocketListener(private val emitter: FlowableEmitter<RxWebSocket>) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        emitter.onNext(RxWebSocket.Opened(webSocket, response))
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        emitter.onNext(RxWebSocket.Failure(webSocket, t, response))
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        emitter.onNext(RxWebSocket.Closing(webSocket, code, reason))
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        emitter.onNext(RxWebSocket.StringMessage(webSocket, text))
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        emitter.onNext(RxWebSocket.BinaryMessage(webSocket, bytes))
    }

    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        emitter.onNext(RxWebSocket.Closed(webSocket, code, reason))
    }

}