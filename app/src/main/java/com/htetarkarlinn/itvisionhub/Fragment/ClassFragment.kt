package com.htetarkarlinn.itvisionhub.Fragment

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
import com.htetarkarlinn.itvisionhub.Activities.AddCampActivity
import com.htetarkarlinn.itvisionhub.Models.AddClass
import com.htetarkarlinn.itvisionhub.Models.CampModel
import com.htetarkarlinn.itvisionhub.Models.User
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        classViewModel =
            ViewModelProvider(this).get(ClassViewModel::class.java)
        _binding = FragmentClassBinding.inflate(inflater, container, false)
        val root = binding.root
        val d = Log.d("HH", "HH")
        val pre: SharedPreferences =
            this.activity!!.getSharedPreferences("Role", AppCompatActivity.MODE_PRIVATE)
        role = pre.getString("role", "string").toString()
        if (role.equals("Student")) {
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
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /* val builder = AlertDialog.Builder(this.activity!!,R.style.CustomAlertDialog)
                .create()
            val view = layoutInflater.inflate(R.layout.add_post,null)
            // val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
            b= AddPostBinding.bind(view)
            builder.setView(view)
            b.classImg.setOnClickListener {
                // requestPermit()
                startImagePick()
            }
            b.cancelPost.setOnClickListener {
                builder.dismiss()
            }
            b.addClass.setOnClickListener {
                val date_time=getCurrentDate()
                val class_name=b.edtName.text.toString()
                val teacher_name=b.teacherName.text.toString()
                val phone_no=b.classPhone.text.toString()
                val descrip=b.description.text.toString()
                val class_id=(0..9999999999).random()
                // Toast.makeText(this.activity, class_id.toString(), Toast.LENGTH_SHORT).show()
                var addclass =AddClass(class_id.toString(),class_name,teacher_name,phone_no,descrip,"",date_time)
                if (class_name=="" && teacher_name=="" && phone_no=="" && descrip==""){
                    Toast.makeText(this.activity, "Please Enter Full Information", Toast.LENGTH_SHORT).show()
                }else{
                    if (uri!=null) {
                        ImageUploadAndDataSave(addclass)
                        uri= null
                    }else{
                        DataSaveNoImage(addclass)
                    }
                    builder.dismiss()

                }
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()

            */

  /*  private fun refreshRecycle() {
        //zay  Toast.makeText(this.activity, "Loading", Toast.LENGTH_SHORT).show()
        class_list.clear()
        val db=Firebase.firestore
        db.collection("class")
            .get().addOnSuccessListener {
                for (doc in it){

                    val clazz=AddClass(doc.id,doc["class_name"].toString(),doc["teacher_name"].toString(),doc["phone"].toString(),doc["description"].toString(),doc["image_url"].toString(),doc["date_time"].toString())
                    class_list.add(clazz)
                }
                new_class_list= class_list
                classAdapter?.notifyDataSetChanged()
                binding.switchToRefresh.isRefreshing=false
            }
            .addOnFailureListener {

            }
    }



    private fun requestPermit() {
        ActivityCompat.requestPermissions(
            this.activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),101
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            101 ->{
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this.activity, "Permission Deny", Toast.LENGTH_SHORT).show()
                }else{
                    startImagePick()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startImagePick() {
        val intent= Intent(Intent.EXTRA_ALLOW_MULTIPLE)
        intent.type="image/*"
        *//*intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setAction(Intent.ACTION_GET_CONTENT)*//*
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action=Intent.ACTION_GET_CONTENT
        activityResult.launch(Intent.createChooser(intent,"Choose Image"))
    }

    private val activityResult=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if (result.resultCode==RESULT_OK && result.data!= null){
            val count= result.data!!.clipData!!.itemCount
            for (i in 0 until  count){
                val image_url=result.data!!.clipData!!.getItemAt(i)
                imagelist.add(image_url.uri)
            }
            Toast.makeText(this.activity, imagelist.size.toString()+" size", Toast.LENGTH_SHORT).show()
            updateProfileImage(result.data!!)
        }
    }

    private fun updateProfileImage(data: Intent) {
        data.data?.let { imageUrl ->
            uri=imageUrl
            //  Toast.makeText(this.activity, "Hello ${data.toString()}", Toast.LENGTH_LONG).show()
            b.classImg.setImageBitmap(getProfileBitmap(imageUrl))
        }
    }
    private fun getProfileBitmap(imageUrl: Uri): Bitmap? {
        return if (imageUrl!=null){
            val inputStream =this.activity?.contentResolver?.openInputStream(imageUrl)
            BitmapFactory.decodeStream(inputStream)
        }else{
            BitmapFactory.decodeResource(resources,R.drawable.add_photo)
        }
    }

    *//*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
          if (requestCode== 100 && resultCode == Activity.RESULT_OK && data!= null){
              uri=data.data
              val inputStr : InputStream? =this.activity!!.contentResolver.openInputStream(uri!!)
              val bitmap = BitmapFactory.decodeStream(inputStr)
              b.classImg.setImageBitmap(bitmap)
          }

          super.onActivityResult(requestCode, resultCode, data)
      }*//*


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun ImageUploadAndDataSave(addclass: AddClass) {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference(("/class_images/$filename"))
        ref.putFile(uri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    addclass.image_url=it.toString()
                    val db = Firebase.firestore

                    db.collection("class")
                        .add(addclass)
                        .addOnSuccessListener { documentReference ->

                            Log.d(
                                ContentValues.TAG,
                                "DocumentSnapshot added with ID: ${addclass.id}"
                            )

                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error adding document", e)
                        }
                }
            }
            .addOnFailureListener{
                Toast.makeText(this.activity, "Image Upload Fail ${it.message}", Toast.LENGTH_SHORT).show()
            }


    }
    private fun DataSaveNoImage(addclass: AddClass) {
        val db = Firebase.firestore

        db.collection("class")
            .add(addclass)
            .addOnSuccessListener { documentReference ->

                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot added with ID: ${documentReference.id}"
                )

            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return sdf.format(Date())
    }

*/