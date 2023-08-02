package com.example.goodgun.main_function

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.goodgun.R
import com.example.goodgun.databinding.ActivityGraphBinding

class GraphActivity : AppCompatActivity() {
    lateinit var binding: ActivityGraphBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout() {
        val items = resources.getStringArray(R.array.my_array)
        binding.apply {
            val myAdapter = ArrayAdapter(this@GraphActivity, android.R.layout.simple_spinner_dropdown_item, items)

            spinner.adapter = myAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {

                    //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                    when (position) {
                        0 -> {

                        }
                        1 -> {

                        }
                        //...
                        else -> {

                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }
    }
}

