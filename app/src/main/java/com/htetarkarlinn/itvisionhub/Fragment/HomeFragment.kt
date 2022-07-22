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
import com.htetarkarlinn.itvisionhub.MainActivity
import com.htetarkarlinn.itvisionhub.databinding.FragmentHomeBinding
import com.ouattararomuald.slider.SliderAdapter
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

        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}