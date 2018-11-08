package com.hocluan.musicfinder

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.acrcloud.rec.sdk.ACRCloudClient
import com.acrcloud.rec.sdk.ACRCloudConfig
import com.acrcloud.rec.sdk.IACRCloudListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), IACRCloudListener {

    var mClient: ACRCloudClient? = null
    var mConfig: ACRCloudConfig? = null
    var startTime: Long = 0
    var mProcessing: Boolean = false
    var initState: Boolean = false
    var path: String = ""

    companion object {
        val MUSIC_DETAIL_KEY = "MUSIC_DETAIL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Transparent Status Bar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        // Hide the Navigation Bar
        supportActionBar?.hide()

        this.setupACRCEnvironment()
        start_button.setOnClickListener {
            this.start()

        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            val zoomOut_animation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
            start_button.startAnimation(zoomOut_animation)
            zoomOut_animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    start_button.text = "Start"
                    start_button.setTextColor(Color.WHITE)
                    start_button.setBackgroundResource(R.drawable.circle_button_accent_color)
                }

                override fun onAnimationStart(animation: Animation?) {
                    start_button.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
                }

            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this.mClient != null) {
            this.mClient?.release()
            this.initState = false
            this.mClient = null
        }
    }
    
    // MARK: - Functions
    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    private fun setupACRCEnvironment() {
        this.path = Environment.getExternalStorageDirectory().toString() + "/acrcloud/model"
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        this.mConfig = ACRCloudConfig()
        this.mClient = ACRCloudClient()
        if (this.mConfig != null && this.mClient != null) {
            this.mConfig?.acrcloudListener = this
            this.mConfig?.context = this
            this.mConfig?.host = "identify-ap-southeast-1.acrcloud.com"
            this.mConfig?.dbPath = path
            this.mConfig?.accessKey = "45f592f387020eb04aeb65b51cd8bab6"
            this.mConfig?.accessSecret = "JYlAx8v8gJCSvHTwXe3ZLmKrEDFUeIEHuQVjq3xU"
            this.mConfig?.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTP
            this.mConfig?.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE

            this.initState = this.mClient?.initWithConfig(this.mConfig) ?: false
            if (this.initState) {
                this.mClient?.startPreRecord(3000)
            }
        }
    }

    private fun start() {
        if (!this.initState) {
            Toast.makeText(this, "init error", Toast.LENGTH_SHORT).show()
            return
        }

        if (!this.mProcessing) {
            this.mProcessing = true
            if (this.mClient == null || !(this.mClient?.startRecognize() ?: true)) {
                mProcessing = false
                Toast.makeText(this,"Something wrong", Toast.LENGTH_SHORT).show()
            }
            this.startTime = System.currentTimeMillis()
        }
    }

    // MARK: - IACRCloudListener
    override fun onResult(p0: String?) {
        if (this.mClient != null) {
            this.mClient?.cancel()
            this.mProcessing = false
        }


        val animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        start_button.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                val intent = Intent(this@MainActivity, MusicDetailActivity::class.java)
                intent.putExtra(MUSIC_DETAIL_KEY, p0 ?: "")
                startActivity(intent)
                overridePendingTransition(0, 0)
            }

            @SuppressLint("ResourceAsColor")
            override fun onAnimationStart(animation: Animation?) {
                start_button.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
            }

        })
    }

    override fun onVolumeChanged(p0: Double) {
        val time = (System.currentTimeMillis() - this.startTime)
        val formatter = SimpleDateFormat("mm:ss")
        val dateString = formatter.format(Date(time))
        start_button.text = dateString

    }


}


