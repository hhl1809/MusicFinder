package com.hocluan.musicfinder

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import com.github.pwittchen.swipe.library.rx2.Swipe
import com.github.pwittchen.swipe.library.rx2.SwipeListener
import kotlinx.android.synthetic.main.activity_music_play.*

class MusicPlayActivity : AppCompatActivity() {

    // MARK: - Properties
    private lateinit var swipe: Swipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_play)

        setupWebView()

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
        handleSwipeEvent()
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        finish()
        overridePendingTransition( R.anim.stay, R.anim.slide_down)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        swipe.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    // MARK: - Functions
    private fun handleSwipeEvent() {
        swipe = Swipe()
        swipe.setListener(object: SwipeListener {
            override fun onSwipedUp(event: MotionEvent?): Boolean {
                return false
            }

            override fun onSwipedDown(event: MotionEvent?): Boolean {
                finish()
                overridePendingTransition( R.anim.stay, R.anim.slide_down)
                return false
            }

            override fun onSwipingUp(event: MotionEvent?) {}

            override fun onSwipedRight(event: MotionEvent?): Boolean {
                return false
            }

            override fun onSwipingLeft(event: MotionEvent?) {}

            override fun onSwipingRight(event: MotionEvent?) {}

            override fun onSwipingDown(event: MotionEvent?) {

            }

            override fun onSwipedLeft(event: MotionEvent?): Boolean {
                return false
            }

        })
    }

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

    private fun setupWebView() {
//        val embedUrl = "<iframe width=\"100%\" height=\"600\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/301161123&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true&visual=true\"></iframe>"
//
//        val html = "<!DOCTYPE html>" +
//                        "<html> " +
//                            "<head> " +
//                                "<meta charset=\"UTF-8\">" +
//                                "<meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> " +
//                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> " +
//                                "<link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" />" +
//                            "</head> " +
//                            "<body style=\"background:grey;margin:0 0 0 0; padding:0 0 0 0;\">" +
//                                 embedUrl +
//                                "<script src=\"https://w.soundcloud.com/player/api.js\" type=\"text/javascript\"></script> " +
//                            "</body> " +
//                        "</html> "
        val url = "https://m.soundcloud.com/edsheeran/shape-of-you"

        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
//        webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "")
        webView.loadUrl(url)

    }
}

