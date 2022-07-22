package com.htetarkarlinn.itvisionhub.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.Models.User
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.adapter.StudentAdapter
import com.htetarkarlinn.itvisionhub.databinding.FragmentClassBinding
import com.htetarkarlinn.itvisionhub.databinding.FragmentListBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {
    private lateinit var listViewModel: ListViewModel
    private var _binding : FragmentListBinding? = null
    private val binding get() = _binding!!
    var user_list : MutableList<User> =ArrayList()
    private var stu_adapter: StudentAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listViewModel =
            ViewModelProvider(this).get(ListViewModel::class.java)
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root=binding.root
        val d = Log.d("HH", "HH")

       // val textView: TextView = binding.textl
     /*   listViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        val db=Firebase.firestore
        db.collection("users")
            .get().addOnSuccessListener {
                for (doc in it){
                    if (!doc["role"].toString().equals("Admin")){
                    val user = User(doc["name"].toString(),doc["phone"].toString(),doc["email"].toString(),doc["password"].toString()
                    ,doc["img"].toString(),doc["role"].toString(),doc["degree"].toString(),doc["camp"].toString())
                   // val user : User =doc.toObject(User::class.java)
                    user_list.add(user)
                    }
                }
               // Toast.makeText(this.activity, "Total Users is ${user_list.size}", Toast.LENGTH_SHORT).show()
                ShowRecycler(user_list)

            }
            .addOnFailureListener{

            }

        return root

    }

    private fun ShowRecycler(userList: MutableList<User>) {
        binding.listRecycle.layoutManager=LinearLayoutManager(this.activity)
        stu_adapter= StudentAdapter(userList,this.activity)
        binding.listRecycle.adapter=stu_adapter
    }


}