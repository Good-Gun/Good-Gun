package com.example.goodgun.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.goodgun.databinding.FragmentHomeFoodBinding
import com.example.goodgun.roomDB.FoodEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.roundToInt

class HomeFoodFragment : Fragment() {

    var binding: FragmentHomeFoodBinding? = null
    private var food: FoodEntity? = null
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
            tvCal.text = food?.calory!!.roundToInt().toString()
            tvC.text = food?.carbohydrates!!.roundToInt().toString()
            tvF.text = food?.fat!!.roundToInt().toString()
            tvP.text = food?.protein!!.roundToInt().toString()
        }
        return binding!!.root
    }
    fun setFood(food: FoodEntity) {
        this.food = food
    }
}
