package com.example.goodgun.main_function

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.goodgun.databinding.FragmentFoodModifyDialogBinding
import com.example.goodgun.roomDB.DatabaseManager
import com.example.goodgun.roomDB.FoodDatabase
import com.example.goodgun.roomDB.FoodEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoodModifyDialog(val food: FoodEntity) : DialogFragment() {
    private var binding: FragmentFoodModifyDialogBinding? = null
    private var roomDB: FoodDatabase? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var userId: String? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 다이얼로그를 생성하고 설정합니다.
        binding = FragmentFoodModifyDialogBinding.inflate(layoutInflater)
        auth = Firebase.auth
        currentUser = auth!!.currentUser
        userId = currentUser!!.uid
        roomDB = DatabaseManager.getDatabaseInstance(userId!!, requireActivity())

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(binding!!.root)
            .setPositiveButton("확인") { dialog, _ ->
                try {
                    food.apply {
                        calory = binding!!.calory.text.toString().toDouble()
                        carbohydrates = binding!!.carbohydrates.text.toString().toDouble()
                        sugar = binding!!.sugar.text.toString().toDouble()
                        protein = binding!!.protein.text.toString().toDouble()
                        fat = binding!!.fat.text.toString().toDouble()
                        trans_fat = binding!!.transFat.text.toString().toDouble()
                        saturated_fat = binding!!.saturatedFat.text.toString().toDouble()
                        cholesterol = binding!!.cholesterol.text.toString().toDouble()
                        sodium = binding!!.sodium.text.toString().toDouble()
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        roomDB!!.foodDao().saveFood(food)
                    }
                } catch (e: Exception) {
                    val errorDialog = AlertDialog.Builder(this@FoodModifyDialog.context)
                        .setMessage("문자가 입력됐거나 작성하지 않은 칸이 있습니다.")
                        .setNegativeButton("확인") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }.create()
        initLayout()
        return dialog
    }

    private fun initLayout() {
        binding!!.apply {
            foodName.text = food.name
            foodName.isSelected = true
            calory.setText(food.calory.toString())
            carbohydrates.setText(food.carbohydrates.toString())
            sugar.setText(food.sugar.toString())
            protein.setText(food.protein.toString())
            fat.setText(food.fat.toString())
            transFat.setText(food.trans_fat.toString())
            saturatedFat.setText(food.saturated_fat.toString())
            cholesterol.setText(food.cholesterol.toString())
            sodium.setText(food.sodium.toString())
            backBtn.setOnClickListener {
                dialog!!.dismiss()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        // 다이얼로그가 취소됐을 때 처리
        super.onCancel(dialog)
    }
}
