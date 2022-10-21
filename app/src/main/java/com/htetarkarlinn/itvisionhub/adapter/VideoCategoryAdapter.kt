package com.htetarkarlinn.itvisionhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.htetarkarlinn.itvisionhub.activities.ShowActivity
import com.htetarkarlinn.itvisionhub.models.VideoCategory
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.VideoCategoryBinding

class VideoCategoryAdapter(val activity: Context?,val videoCategory: ArrayList<VideoCategory>,val idList : ArrayList<String>,val role : String,val camp : String):RecyclerView.Adapter<VideoCategoryAdapter.ViewHolder>() {
    class ViewHolder (itemView : View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.video_category,parent,false)
        return VideoCategoryAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding : VideoCategoryBinding=VideoCategoryBinding.bind(holder.itemView)
        binding.title.text=videoCategory[position].name
        if (videoCategory[position].forShow.equals("public")){
            binding.premium.visibility=View.INVISIBLE
        }
       // Toast.makeText(activity, camp, Toast.LENGTH_SHORT).show()
        holder.itemView.setOnClickListener {
            if (role == "Student" && videoCategory[position].forShow.equals("private") && camp == ""){
                Toast.makeText(activity, "This category is private.So you need to attend camp", Toast.LENGTH_SHORT).show()
            }else {
                //Toast.makeText(activity, "${idList[position]}", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, ShowActivity::class.java)
                intent.putExtra("category", videoCategory[position])
                intent.putExtra("catId", idList[position])
                activity?.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return videoCategory.size
    }

}
