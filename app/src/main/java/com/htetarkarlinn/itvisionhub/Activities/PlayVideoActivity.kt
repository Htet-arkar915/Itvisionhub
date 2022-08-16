package com.htetarkarlinn.itvisionhub.Activities

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.ActivityPlayVideoBinding

class PlayVideoActivity : AppCompatActivity() {
    lateinit var binding : ActivityPlayVideoBinding
    var videoId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoId= intent.getStringExtra("id")!!
        val title=intent.getStringExtra("title")
        supportActionBar?.title=title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val videoID_Lan="<html><body><iframe width=\"800\" height=\"330\" src=\"https://www.youtube.com/embed/${videoId}\" frameborder=\"2\" allowfullscreen=true></iframe></body></html>"

        val videoID="<html><body><iframe width=\"385\" height=\"225\" src=\"https://www.youtube.com/embed/${videoId}\" frameborder=\"2\" allowfullscreen=true></iframe></body></html>"
        binding.videoPlay.settings.javaScriptEnabled=true
        binding.videoPlay.settings.pluginState= WebSettings.PluginState.ON
        binding.videoPlay.background=getDrawable(R.drawable.transparent_bg)
        if (orientation.equals("Portrait")) {
            supportActionBar?.show()
            binding.videoPlay.loadData(videoID, "text/html", "utf-8")
        }else{
            supportActionBar?.hide()
            binding.videoPlay.loadData(videoID_Lan, "text/html", "utf-8")
            window.decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        binding.videoPlay.webChromeClient= WebChromeClient()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
val Context.orientation:String
    get() {
        return when(resources.configuration.orientation){
            Configuration.ORIENTATION_PORTRAIT -> "Portrait"
            Configuration.ORIENTATION_LANDSCAPE -> "Landscape"
            Configuration.ORIENTATION_UNDEFINED -> "Undefined"
            else -> "Error"
        }
    }