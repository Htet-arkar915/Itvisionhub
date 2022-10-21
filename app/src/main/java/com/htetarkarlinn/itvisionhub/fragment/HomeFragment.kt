package com.htetarkarlinn.itvisionhub.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.models.VideoCategory
import com.htetarkarlinn.itvisionhub.models.VideoModel
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.`object`.Config
import com.htetarkarlinn.itvisionhub.adapter.VideoCategoryAdapter
import com.htetarkarlinn.itvisionhub.databinding.AddVideoCategoryBinding
import com.htetarkarlinn.itvisionhub.databinding.FragmentHomeBinding
import com.htetarkarlinn.itvisionhub.fragment.HomeViewModel as HomeViewModel1

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel1
    private var _binding :FragmentHomeBinding? = null
    private val binding get() = _binding!!
    var role=""
    var camp=""
    var catIdList : ArrayList<String> = arrayListOf()
    var filterCatIdList : ArrayList<String> = arrayListOf()
    private var videoCategoryAdapter : VideoCategoryAdapter?=null
    val videoCategory: ArrayList<VideoCategory> = arrayListOf()
    val filterVideoCategory: ArrayList<VideoCategory> = arrayListOf()
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel1::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root=binding.root
        Log.d("HH", "HH")
        val prefer : SharedPreferences =this.requireActivity().getSharedPreferences("Role", AppCompatActivity.MODE_PRIVATE)
        role=prefer.getString("role","string").toString()
        camp=prefer.getString("camp","string").toString()
        videoCategory.clear()
        collectCategory(binding)
        binding.collapsLayout.title="ITVision Hub"

        /*if (binding.collapsLayout.title.toString().equals("Videos Categories")){

        }*/
        binding.floatBtn.setOnClickListener {
            showAddCategoryDialog(binding)
        }

        binding.searchView.layoutParams= Toolbar.LayoutParams(Gravity.END)
        binding.searchView.queryHint="Search category"
        
        binding.searchView.setOnSearchClickListener {
            binding.searchView.background=activity?.getDrawable(R.drawable.search_view_bg)

        }
        binding.searchView.setOnCloseListener {
            binding.searchView.background = activity?.getDrawable(R.drawable.search_transparentbg)
            false
        }
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                for (category in videoCategory){
                    if (category.name.equals(query)){
                        filterVideoCategory.add(category)
                        filterCatIdList.add(catIdList[videoCategory.indexOf(category)])
                    }
                }
                binding.videoRecycler.layoutManager=GridLayoutManager(activity,2)
                videoCategoryAdapter= VideoCategoryAdapter(activity,filterVideoCategory,filterCatIdList,role,camp)
                binding.videoRecycler.adapter=videoCategoryAdapter
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCatIdList.clear()
                filterVideoCategory.clear()
               for (category in videoCategory){
                   if (category.name!!.lowercase().contains(newText!!.lowercase())){
                       filterVideoCategory.add(category)
                       filterCatIdList.add(catIdList[videoCategory.indexOf(category)])
                       }
               }
                binding.videoRecycler.layoutManager=GridLayoutManager(activity,2)
                videoCategoryAdapter= VideoCategoryAdapter(activity,filterVideoCategory,filterCatIdList,role,camp)
                binding.videoRecycler.adapter=videoCategoryAdapter
                return true
            }

        })

        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (verticalOffset==0){
                binding.searchView.visibility=View.VISIBLE
                binding.collapsLayout.title="ITVision Hub"
                if (role != "Admin"){
                    binding.floatBtn.visibility=View.INVISIBLE
                }else {
                    binding.floatBtn.visibility = View.VISIBLE
                }
            }else{
               // binding.searchView.visibility=View.INVISIBLE
                binding.floatBtn.visibility=View.INVISIBLE
                binding.collapsLayout.title="Video Categories"
            }
        })

        return root

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun collectCategory(b: FragmentHomeBinding) {
       // var videoList : MutableList<VideoModel> = arrayListOf()
        if (!Config.isNetworkConnected(this.requireActivity())){
            Toast.makeText(this.requireActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show()
        }else {
            catIdList.clear()
            videoCategory.clear()
            val db = Firebase.firestore
            db.collection("Video")
                .get().addOnSuccessListener {
                    for (doc in it) {
                        catIdList.add(doc.id)
                        // val category=VideoCategory(doc["name"].toString(),doc["forShow"].toString(),doc["videoList"] as ArrayList<VideoModel>? /* = java.util.ArrayList<com.htetarkarlinn.itvisionhub.Models.VideoModel> */)
                        val category = doc.toObject(VideoCategory::class.java)
                        videoCategory.add(category)
                        // videoList =doc.data.getValue("videoList") as MutableList<VideoModel>

                    }
                    // Toast.makeText(this.activity, "${videoList[0]!!.videoId}", Toast.LENGTH_SHORT).show()
                    b.pg.visibility = View.INVISIBLE
                    showRecycle(b)
                }
                .addOnFailureListener {

                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showAddCategoryDialog(fb: FragmentHomeBinding) {
        val builder=AlertDialog.Builder(this.requireActivity(), R.style.CustomAlertDialog).create()
        val view=layoutInflater.inflate(R.layout.add_video_category,null)
        val b=AddVideoCategoryBinding.bind(view)
        builder.setView(view)

        b.btnCancel.setOnClickListener {
            builder.dismiss()
        }
        b.btnAdd.setOnClickListener {
            val category = b.cateName.text.toString()
            var forShow = ""
            if (category == "") {
                Toast.makeText(this.activity, "Enter category", Toast.LENGTH_SHORT).show()
            } else {
                if (b.btnPublic.isChecked) {
                    forShow = "public"
                    Storecategory(fb,b,builder,category,forShow)
                } else if (b.btnPrivate.isChecked) {
                    forShow = "private"
                    Storecategory(fb,b,builder,category,forShow)
                } else {
                    Toast.makeText(
                        this.activity,
                        "Please select public or private",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        builder.setCancelable(false)
        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun Storecategory(fb : FragmentHomeBinding, b: AddVideoCategoryBinding, builder: AlertDialog, category: String, forShow: String) {
        b.btnCancel.isEnabled=false
        b.btnAdd.text="Adding"
        b.btnAdd.isEnabled=false
        val videoList : ArrayList<VideoModel> = arrayListOf()
        val cate=VideoCategory(category,forShow,videoList)
        val db=Firebase.firestore
        db.collection("Video")
            .add(cate)
            .addOnSuccessListener {
                builder.dismiss()
                collectCategory(fb)
            }
            .addOnFailureListener {
                b.btnCancel.isEnabled=true
                b.btnAdd.text="Add"
                b.btnAdd.isEnabled=true
                Toast.makeText(this.activity, "Check internet", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showRecycle(b: FragmentHomeBinding) {
        //Toast.makeText(this.activity, "${videoCategory[1].videoList!![0].title}", Toast.LENGTH_SHORT).show()
        b.videoRecycler.layoutManager=GridLayoutManager(this.activity,2)
        videoCategoryAdapter= VideoCategoryAdapter(this.activity,videoCategory,catIdList,role,camp)
        b.videoRecycler.adapter=videoCategoryAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
/*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu,menu)
        val search=menu.findItem(R.id.id_search)
        val searchView=search.actionView as SearchView
        searchView.queryHint="Search Category"
        //activity?.actionBar?.setHomeAsUpIndicator(R.menu.search_menu)
        return
    }*/
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.search_menu,menu)
    }

}

