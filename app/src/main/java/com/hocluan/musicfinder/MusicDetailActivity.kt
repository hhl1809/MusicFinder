package com.hocluan.musicfinder

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_music_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MusicDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_detail)

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
        val jsonString = intent.getStringExtra(MainActivity.MUSIC_DETAIL_KEY)
        fetchJson(jsonString)

    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
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

    private  fun fetchJson(result: String) {
        val json = JSONObject(result)

        try {
            if (json.has("status")) {
                val status = json.getJSONObject("status")
                val code = status.getInt("code")
                if (code == 0) {
                    val metadata = json.getJSONObject("metadata")
                    if (metadata.has("music")) {
                        val musics: JSONArray = metadata.getJSONArray("music")
                        if (musics.length() > 0) {
                            val music = musics.getJSONObject(0)
                            val title = music.getString("title")
                            val artist = (music.getJSONArray("artists")[0] as JSONObject).getString("name")
                            this.setTextToResultTextView(title, artist)
                        }
                    }
                } else {
                    if (status.has("msg")) {
                        val msg: String = status.getString("msg")
                        this.setTextToResultTextView(msg, "")
                    }

                }
            }

        } catch (e: JSONException) {
            if (json.has("status")) {
                val status = json.getJSONObject("status")
                val code = status.getInt("code")
                if (code != 0) {
                    if (status.has("msg")) {
                        val msg: String = status.getString("msg")
                        this.setTextToResultTextView(msg, "")
                    }
                    e.printStackTrace()
                }

            }
        }
    }

    private fun setTextToResultTextView(result: String, description: String) {
        result_textView.text = result
        description_textView.text = description
        val animation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        val interpolator = BounceInterpolator(0.2, 20.0)
        animation.setInterpolator(interpolator)
        result_textView.startAnimation(animation)
        description_textView.startAnimation(animation)

    }
}
