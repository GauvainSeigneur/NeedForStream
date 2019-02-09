package com.seigneur.gauvain.needforstream.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
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

    private val mMainViewModel by lazy {
        MainViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mMainViewModel.init()
    }


}
