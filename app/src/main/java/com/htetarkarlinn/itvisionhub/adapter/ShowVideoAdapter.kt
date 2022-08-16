package com.htetarkarlinn.itvisionhub.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import androidx.recyclerview.widget.RecyclerView
import com.htetarkarlinn.itvisionhub.Activities.PlayVideoActivity
import com.htetarkarlinn.itvisionhub.Activities.ShowActivity
import com.htetarkarlinn.itvisionhub.Models.VideoCategory
import com.htetarkarlinn.itvisionhub.Models.VideoModel
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.ShowVideoBinding

class ShowVideoAdapter(var showActivity: ShowActivity, private val videoCategory: VideoCategory): RecyclerView.Adapter<ShowVideoAdapter.MyHolder>() {
    class MyHolder (itemView : View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.show_video,parent,false)
        return ShowVideoAdapter.MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        var videoId=""
        val videoList : List<VideoModel> =videoCategory.videoList as List<VideoModel> /* = java.util.ArrayList<com.htetarkarlinn.itvisionhub.Models.VideoModel> */        //val video_list : MutableList<VideoModel> = videoList as MutableList<VideoModel>
//        val videos: VideoModel=videoList[position] as VideoModel
        //var video =VideoModel(video_list[position].videoId,video_list[position].title)
        //Toast.makeText(showActivity, "${videoList[position].videoId}", Toast.LENGTH_SHORT).show()
        val binding : ShowVideoBinding = ShowVideoBinding.bind(holder.itemView)
        val model : VideoModel=videoList[position] as VideoModel
        binding.title.text= videoList[position].title
        videoId= videoList[position].videoId!!
        val videoID="<html><body><br><iframe width=\"380\" height=\"220\" src=\"https://www.youtube.com/embed/${videoId}\" frameborder=\"2\" allowfullscreen=true></iframe></body></html>"
         //binding.showVideo.settings.javaScriptEnabled=true
        // binding.showVideo.settings.pluginState= WebSettings.PluginState.ON
       //  binding.showVideo.loadData(videoID,"text/html","utf-8")
       //  binding.showVideo.webChromeClient= WebChromeClient()
        holder.itemView.setOnClickListener {
            val intent=Intent(showActivity, PlayVideoActivity::class.java)
            intent.putExtra("id",videoId)
            intent.putExtra("title",videoList[position].title)
            showActivity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        val videoList : ArrayList<VideoModel> =videoCategory.videoList as ArrayList<VideoModel>
        return videoList.size
    }

}
