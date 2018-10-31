package com.hocluan.musicfinder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_music_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MusicDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_detail)
        // Hide the Navigation Bar
        supportActionBar?.hide()
        val jsonString = intent.getStringExtra(MainActivity.MUSIC_DETAIL_KEY)
        fetchJson(jsonString)
    }

    private  fun fetchJson(result: String) {
        try {
            val json = JSONObject(result)
            val status = json.getJSONObject("status")
            val code = status.getInt("code")
            if (code == 0) {
                val metadata = json.getJSONObject("metadata")
                if (metadata.has("music")) {
                    val musics: JSONArray = metadata.getJSONArray("music")
                    if (musics.length() > 0) {
                        val title = musics.getJSONObject(0).getString("title")
                        result_textView.text = title
                    }
                }
            } else {
                result_textView.text = result
            }
        } catch (e: JSONException) {
            result_textView.text = result
            e.printStackTrace()
        }

    }
}
