package com.example.goodgun.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.goodgun.databinding.FragmentHomeFoodBinding
import com.example.goodgun.network.model.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFoodFragment : Fragment() {

    var binding: FragmentHomeFoodBinding? = null
    private var food: Food? = null
    var database: DatabaseReference? = null
    private var auth: FirebaseAuth? = null
    var currentUser: FirebaseUser? = null
    var userid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeFoodBinding.inflate(layoutInflater, container, false)
        database = Firebase.database("https://goodgun-4740f-default-rtdb.firebaseio.com/").reference
        auth = Firebase.auth
        currentUser = auth!!.currentUser
        if (currentUser == null) {
            Toast.makeText(requireActivity(), "유효하지 않은 유저입니다.", Toast.LENGTH_SHORT).show()
        } else {
            userid = currentUser!!.uid
        }
        binding?.apply {
            tvName.text = food?.name
            tvCal.text = food?.calory.toString()
            tvC.text = food?.carbohydrates.toString()
            tvF.text = food?.fat.toString()
            tvP.text = food?.protein.toString()
        }
        return binding!!.root
    }
    fun setFood(food: Food) {
        this.food = food
    }
}
