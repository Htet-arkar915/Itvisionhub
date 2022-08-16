package com.htetarkarlinn.itvisionhub.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.ShowImageBinding
import com.squareup.picasso.Picasso

class CampImageAdapter(val context: Context,val imageList: MutableList<Uri>): RecyclerView.Adapter<CampImageAdapter.myViewHolder>() {
    class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding= ShowImageBinding.bind(view)
        // val binding : ShowImageBinding.bind(view)
        fun bind(binding: ShowImageBinding, it: Uri?) {
           Picasso.get().load(it.toString()).placeholder(R.drawable.cover).into(binding.showImg)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.show_image,parent,false)
        return CampImageAdapter.myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        imageList?.get(position).let { holder.bind(holder.binding,it) }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}
