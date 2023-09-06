package com.example.goodgun.main_function

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.goodgun.R
import com.example.goodgun.databinding.FragmentFoodModifyDialogBinding
import com.example.goodgun.roomDB.FoodEntity

class FoodModifyDialog(val food: FoodEntity) : DialogFragment() {
    private var binding: FragmentFoodModifyDialogBinding? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 다이얼로그를 생성하고 설정합니다.
        val builder = AlertDialog.Builder(requireActivity())
            .setView(R.layout.fragment_food_modify_dialog)
            .setPositiveButton("확인") { dialog, _ ->
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // 다이얼로그 레이아웃을 설정하고 다이얼로그 내용을 처리합니다.
        binding = FragmentFoodModifyDialogBinding.inflate(layoutInflater)
        binding!!.apply {
            foodName.text = food.name
            calory.setText(food.calory.toString())
            carbohydrates.setText(food.carbohydrates.toString())
            sugar.setText(food.sugar.toString())
            protein.setText(food.protein.toString())
            fat.setText(food.fat.toString())
            transFat.setText(food.trans_fat.toString())
            saturatedFat.setText(food.saturated_fat.toString())
            cholesterol.setText(food.cholesterol.toString())
//            sodium.setText(food.sodium.toString())
        }
        return binding!!.root
    }

    override fun onCancel(dialog: DialogInterface) {
        // 다이얼로그가 취소됐을 때 처리
        super.onCancel(dialog)
    }
}
