package com.htetarkarlinn.itvisionhub.Fragment

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.Models.VideoCategory
import com.htetarkarlinn.itvisionhub.Models.VideoModel
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.adapter.VideoCategoryAdapter
import com.htetarkarlinn.itvisionhub.databinding.AddVideoCategoryBinding
import com.htetarkarlinn.itvisionhub.databinding.FragmentHomeBinding
import com.htetarkarlinn.itvisionhub.Fragment.HomeViewModel as HomeViewModel1

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

   // private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var homeViewModel: HomeViewModel1
    private var _binding :FragmentHomeBinding? = null
    private val binding get() = _binding!!
    var role=""
    var camp=""
    var search=true
    var catIdList : ArrayList<String> = arrayListOf()
    var filter_catIdList : ArrayList<String> = arrayListOf()
    private var videoCategoryAdapter : VideoCategoryAdapter?=null
    val videoCategory: ArrayList<VideoCategory> = arrayListOf()
    val filter_videoCategory: ArrayList<VideoCategory> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel1::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root=binding.root
        val d = Log.d("HH", "HH")
        //binding.imgSlider.adapter= SliderAdapter()
        /*val textView: TextView = binding.textf
        homeViewModel.text.observe(viewLifecycleOwner, Observer{
            textView.text = it
        })*/
        //this.activity?.actionBar?.title="ITVisionhub"
       // this.activity?.setActionBar(binding.collapsLayout)
        val prefer : SharedPreferences =this.activity!!.getSharedPreferences("Role", AppCompatActivity.MODE_PRIVATE)
        role=prefer.getString("role","string").toString()
        camp=prefer.getString("camp","string").toString()
        videoCategory.clear()
        CollectCategory(binding)
        binding.collapsLayout.title="ITVision Hub"

        /*if (binding.collapsLayout.title.toString().equals("Videos Categories")){

        }*/
        binding.floatBtn.setOnClickListener {
            showAddCategoryDialog(binding)
        }

        binding.searchView.layoutParams= Toolbar.LayoutParams(Gravity.RIGHT)
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
                        filter_videoCategory.add(category)
                        filter_catIdList.add(catIdList[videoCategory.indexOf(category)])
                    }
                }
                binding.videoRecycler.layoutManager=GridLayoutManager(activity,2)
                videoCategoryAdapter= VideoCategoryAdapter(activity,filter_videoCategory,filter_catIdList,role,camp)
                binding.videoRecycler.adapter=videoCategoryAdapter
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter_catIdList.clear()
                filter_videoCategory.clear()
               for (category in videoCategory){
                   if (category.name!!.lowercase().contains(newText!!.lowercase())){
                       filter_videoCategory.add(category)
                       filter_catIdList.add(catIdList[videoCategory.indexOf(category)])
                       }
               }
                binding.videoRecycler.layoutManager=GridLayoutManager(activity,2)
                videoCategoryAdapter= VideoCategoryAdapter(activity,filter_videoCategory,filter_catIdList,role,camp)
                binding.videoRecycler.adapter=videoCategoryAdapter
                return true
            }

        })

        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset==0){
                binding.searchView.visibility=View.VISIBLE
               // binding.collapsLayout.createContextMenu(inflater.(R.menu.search_menu))
                binding.collapsLayout.title="ITVision Hub"
                if (!role.equals("Admin")){
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

    private fun CollectCategory(b: FragmentHomeBinding) {
       // var videoList : MutableList<VideoModel> = arrayListOf()
        catIdList.clear()
        videoCategory.clear()
        val db=Firebase.firestore
        db.collection("Video")
            .get().addOnSuccessListener {
                for (doc in it){
                    catIdList.add(doc.id)
                   // val category=VideoCategory(doc["name"].toString(),doc["forShow"].toString(),doc["videoList"] as ArrayList<VideoModel>? /* = java.util.ArrayList<com.htetarkarlinn.itvisionhub.Models.VideoModel> */)
                    val category=doc.toObject(VideoCategory::class.java)
                    videoCategory.add(category)
                    // videoList =doc.data.getValue("videoList") as MutableList<VideoModel>

                }
               // Toast.makeText(this.activity, "${videoList[0]!!.videoId}", Toast.LENGTH_SHORT).show()
                showRecycle(b)
            }
            .addOnFailureListener {

            }
    }

    private fun showAddCategoryDialog(fb: FragmentHomeBinding) {
        var builder=AlertDialog.Builder(this.activity!!, R.style.CustomAlertDialog).create()
        val view=layoutInflater.inflate(R.layout.add_video_category,null)
        val b=AddVideoCategoryBinding.bind(view)
        builder.setView(view)

        b.btnCancel.setOnClickListener {
            builder.dismiss()
        }
        b.btnAdd.setOnClickListener {
            var category = b.cateName.text.toString()
            var forShow = ""
            if (category.equals("")) {
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

    private fun Storecategory(fb : FragmentHomeBinding,b: AddVideoCategoryBinding ,builder: AlertDialog,category: String, forShow: String) {
        b.btnCancel.isEnabled=false
        b.btnAdd.text="Adding"
        b.btnAdd.isEnabled=false
        var videoList : ArrayList<VideoModel>? = arrayListOf()
        var cate=VideoCategory(category,forShow,videoList)
        val db=Firebase.firestore
        db.collection("Video")
            .add(cate)
            .addOnSuccessListener {
                builder.dismiss()
                CollectCategory(fb)
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

