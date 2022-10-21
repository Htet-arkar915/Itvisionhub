package com.htetarkarlinn.itvisionhub.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.activities.AddCampActivity
import com.htetarkarlinn.itvisionhub.models.AddClass
import com.htetarkarlinn.itvisionhub.models.CampModel
import com.htetarkarlinn.itvisionhub.models.User
import com.htetarkarlinn.itvisionhub.adapter.CampAdapter
import com.htetarkarlinn.itvisionhub.databinding.AddPostBinding
import com.htetarkarlinn.itvisionhub.databinding.FragmentClassBinding
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClassFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClassFragment : Fragment() {
    var uri: Uri? = null
    private lateinit var b: AddPostBinding
    private lateinit var classViewModel: ClassViewModel
    private var _binding: FragmentClassBinding? = null
    private val binding get() = _binding!!
    var camp_list: MutableList<CampModel> = ArrayList()
    var camps: MutableList<CampModel> = ArrayList()
    var camps_id : MutableList<String> =ArrayList()
    var new_class_list: MutableList<AddClass> = ArrayList()
    var imagelist: ArrayList<Uri> = ArrayList()
    private var campAdapter: CampAdapter? = null
    private var role: String = ""

    private var students : MutableList<User> = ArrayList()
    private var id_list : MutableList<String> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        classViewModel =
            ViewModelProvider(this)[ClassViewModel::class.java]
        _binding = FragmentClassBinding.inflate(inflater, container, false)
        val root = binding.root
        val d = Log.d("HH", "HH")
        val pre: SharedPreferences =
            this.requireActivity().getSharedPreferences("Role", AppCompatActivity.MODE_PRIVATE)
        role = pre.getString("role", "string").toString()
        if (role == "Student") {
            binding.addClassBtn.visibility = View.INVISIBLE
        }
        // val textView: TextView = binding.textc
        /*   classViewModel.text.observe(viewLifecycleOwner, Observer {
               textView.text = it
           })*/
        CollectStudentInfo()
        CollectCamps()
        //if (binding.classRecycler.hasPendingAdapterUpdates())

            binding.switchToRefresh.setOnRefreshListener {
                campAdapter?.notifyDataSetChanged()
                binding.switchToRefresh.isRefreshing = true
                CollectStudentInfo()
                CollectCampsForRefresh()

            }
            binding.addClassBtn.setOnClickListener {

                startActivity(Intent(this.activity, AddCampActivity::class.java))

            }

        return root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun CollectCampsForRefresh() {
        camps_id.clear()
        camp_list.clear()
        val db = Firebase.firestore
        db.collection("camp")
            .get().addOnSuccessListener {
                for (doc in it) {
                    camps_id.add(doc.id)
                    val camp = CampModel(doc["start_date"].toString(),doc["end_date"].toString(),
                        doc["camp_name"].toString(),doc["time"].toString(),doc["description"].toString(),
                        doc["images"] as ArrayList<Uri?>?
                    )
                    camp_list.add(camp)
                }
                //new_class_list = class_list.sortedBy { it.date_time } as MutableList<AddClass>
                //new_class_list = new_class_list.asReversed()
                camps=camp_list.distinct().toMutableList()
                campAdapter?.notifyDataSetChanged()
                binding.switchToRefresh.isRefreshing=false
            }
            .addOnFailureListener {

            }
    }


    private fun CollectCamps() {
        camps_id.clear()
        camp_list.clear()
        val db = Firebase.firestore
        db.collection("camp")
            .get().addOnSuccessListener {
                for (doc in it) {
                    camps_id.add(doc.id)
                    val camp = CampModel(doc["start_date"].toString(),doc["end_date"].toString(),
                        doc["camp_name"].toString(),doc["time"].toString(),doc["description"].toString(),
                        doc["images"] as ArrayList<Uri?>?
                    )
                    camp_list.add(camp)
                }
                //new_class_list = class_list.sortedBy { it.date_time } as MutableList<AddClass>
                //new_class_list = new_class_list.asReversed()
                camps=camp_list.distinct().toMutableList()
                showRecycle(camp_list)
                binding.switchToRefresh.isRefreshing=false
            }
            .addOnFailureListener {

            }
    }

    private fun CollectStudentInfo() {
        val db= Firebase.firestore
        db.collection("users")
            .get().addOnSuccessListener {
                for (doc in it){
                    if (!doc["role"].toString().equals("Admin")){
                        id_list.add(doc.id)
                        val user = User(doc["name"].toString(),doc["phone"].toString(),doc["email"].toString(),doc["password"].toString()
                            ,doc["img"].toString(),doc["role"].toString(),doc["degree"].toString(),doc["camp"].toString(),doc["request"].toString(),doc["noti"].toString())
                        // val user : User =doc.toObject(User::class.java)
                        students.add(user)
                    }
                }

            }
            .addOnFailureListener{

            }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showRecycle(camps: MutableList<CampModel>) {
        binding.classRecycler.layoutManager= LinearLayoutManager(this.activity)
        campAdapter= CampAdapter(camps,camps_id,students,id_list,this.activity,role)
        //val recyclerState= (binding.classRecycler.layoutManager as LinearLayoutManager).onSaveInstanceState()
        binding.classRecycler.adapter=campAdapter
        binding.pgb.visibility=View.INVISIBLE
        campAdapter!!.notifyDataSetChanged()
        //(binding.classRecycler.layoutManager as LinearLayoutManager).onRestoreInstanceState(recyclerState)
    }
}