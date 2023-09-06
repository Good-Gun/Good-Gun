package com.example.goodgun.main

import android.os.Bundle
import android.util.Log
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
            tvCal.text = food?.calory.toString()
            tvC.text = food?.carbohydrates.toString()
            tvF.text = food?.fat.toString()
            tvP.text = food?.protein.toString()
            fragment.setOnClickListener {
                Log.i("food", food!!.name)
                val nowFood = database!!.child("user_list").child(userid!!).child("food_list")
                    .child(food!!.registerDate)
                    .child(food!!.name).get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            val dataSnapshot = it.result
                            for (child in dataSnapshot.children) {
                                val value = child.getValue(FoodEntity::class.java)
                                Log.i("food", value.toString())
                            }
                        } else {
                            Log.e("food", "error")
                        }
                    }.addOnFailureListener {
                        Log.e("food", it.toString())
                    }
            }
        }
        return binding!!.root
    }
    fun setFood(food: FoodEntity) {
        this.food = food
    }
}
