package com.example.goodgun

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.goodgun.databinding.FragmentHomeBinding
import com.example.goodgun.main_function.FoodActivity
import com.example.goodgun.main_function.GraphActivity


class HomeFragment : Fragment() {

    var binding: FragmentHomeBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        initLayout()
        setProgress()

    }

    fun initLayout(){
        binding!!.homeBtn2.setOnClickListener {
            val intent = Intent(activity, FoodActivity::class.java)
            startActivity(intent)
        }
        binding!!.homeBtn3.setOnClickListener {
            val intent = Intent(activity, GraphActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setProgress(){
        binding!!.pbHomeCalorie.setProgress(40)
    }


}