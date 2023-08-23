package com.example.goodgun.main_function

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.goodgun.databinding.FragmentSolutionBinding

class SolutionFragment() : Fragment() {

    var binding: FragmentSolutionBinding? = null
    private var title: String? = null
    private var body: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSolutionBinding.inflate(layoutInflater, container, false)
        binding?.apply {
            fragTitle.text = title
            fragBody.text = body
        }
        return binding!!.root
    }
    fun setText(text: String) {
        val str = text.split(":")
        title = str[0].trim()
        body = str[1].trim()
    }
}
