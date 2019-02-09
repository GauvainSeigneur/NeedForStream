package com.seigneur.gauvain.needforstream.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.seigneur.gauvain.needforstream.R
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import okhttp3.WebSocket
import okhttp3.OkHttpClient
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.WebSocketListener


class MainActivity : AppCompatActivity() {

    private val httpLoggingInterceptor= HttpLoggingInterceptor()

    val client by lazy {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start.setOnClickListener {
            start()
        }

    }

    private fun start() {
        val request = Request.Builder().url("ws://pbbouachour.fr:8080/openSocket").build()
        val listener = EchoWebSocketListener()
        val ws = client.newWebSocket(request, listener)
        client.dispatcher().executorService().shutdown()
    }

    private fun sendData(webSocket: WebSocket) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("Type", "infos")
            jsonObject.put("UserToken", 42)
            webSocket.send(jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private inner class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Timber.d("onOpen : $response")
            sendData(webSocket)
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            Timber.d("Receiving : " + text!!)
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
            Timber.d("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            Timber.d("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Timber.d("Error : " + t.message)
        }
    }

    companion object {
        private val NORMAL_CLOSURE_STATUS = 1000
    }

}
