package com.example.goodgun.main_function

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.goodgun.databinding.FragmentFoodModifyDialogBinding
import com.example.goodgun.network.model.Food

class FoodModifyDialog(val food: Food) : DialogFragment() {
    private var binding: FragmentFoodModifyDialogBinding? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 다이얼로그를 생성하고 설정합니다.
        val builder = AlertDialog.Builder(requireActivity())
        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // 다이얼로그 레이아웃을 설정하고 다이얼로그 내용을 처리합니다.
        binding = FragmentFoodModifyDialogBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onCancel(dialog: DialogInterface) {
        // 다이얼로그가 취소됐을 때 처리
        super.onCancel(dialog)
    }
}
