package com.example.goodgun.main_function

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.goodgun.databinding.FragmentFoodBinding

class FoodFragment() : Fragment() {

    var binding: FragmentFoodBinding? = null
    private var text: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFoodBinding.inflate(layoutInflater, container, false)
        binding?.apply {
            fragText.text = text?.trim()
        }
        return binding!!.root
    }
    fun setText(text: String) {
        this.text = text
    }
}
