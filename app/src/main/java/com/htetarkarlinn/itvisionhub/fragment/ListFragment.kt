package com.htetarkarlinn.itvisionhub.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.models.User
import com.htetarkarlinn.itvisionhub.adapter.StudentAdapter
import com.htetarkarlinn.itvisionhub.databinding.FragmentListBinding


class ListFragment : Fragment() {
    private lateinit var listViewModel: ListViewModel
    private var _binding : FragmentListBinding? = null
    private val binding get() = _binding!!
    var userList : MutableList<User> =ArrayList()
    private var stuAdapter: StudentAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listViewModel =
            ViewModelProvider(this)[ListViewModel::class.java]
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root=binding.root
        Log.d("HH", "HH")

       // val textView: TextView = binding.textl
     /*   listViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        val db=Firebase.firestore
        db.collection("users")
            .get().addOnSuccessListener {
                for (doc in it){
                    if (doc["role"].toString() != "Admin"){
                    val user = User(doc["name"].toString(),doc["phone"].toString(),doc["email"].toString(),doc["password"].toString()
                    ,doc["img"].toString(),doc["role"].toString(),doc["degree"].toString(),doc["camp"].toString(),doc["request"].toString(),doc["noti"].toString())
                   // val user : User =doc.toObject(User::class.java)
                    userList.add(user)
                    }
                }
               // Toast.makeText(this.activity, "Total Users is ${userList.size}", Toast.LENGTH_SHORT).show()
                showRecycler(userList)

            }
            .addOnFailureListener{

            }

        return root

    }

    private fun showRecycler(userList: MutableList<User>) {
        binding.listRecycle.layoutManager=LinearLayoutManager(this.activity)
        stuAdapter= StudentAdapter(userList,this.activity)
        binding.listRecycle.adapter=stuAdapter
    }


}