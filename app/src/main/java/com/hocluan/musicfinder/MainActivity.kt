package com.hocluan.musicfinder

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.acrcloud.rec.sdk.ACRCloudClient
import com.acrcloud.rec.sdk.ACRCloudConfig
import com.acrcloud.rec.sdk.IACRCloudListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

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

        // Hide the Navigation Bar
        supportActionBar?.hide()

        this.setupACRCEnvironment()
        start_button.setOnClickListener {
//            this.start()
            val animation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            val interpolator = BounceInterpolator(0.2, 20.0)
            animation.setInterpolator(interpolator)
            start_button.startAnimation(animation)
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

        val intent = Intent(this, MusicDetailActivity::class.java)
        intent.putExtra(MUSIC_DETAIL_KEY, p0 ?: "")
        startActivity(intent)
    }

    override fun onVolumeChanged(p0: Double) {
//        val time = (System.currentTimeMillis() - this.startTime) / 1000
//        time_textView.text = p0.toString() + "\n\n Record Time: " + time.toString() + " s"
    }


}


