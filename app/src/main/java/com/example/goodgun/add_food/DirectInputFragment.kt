
package com.example.goodgun.add_food

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.goodgun.databinding.FragmentDirectInputBinding

class DirectInputFragment : DialogFragment() {
    private var _binding: FragmentDirectInputBinding? = null
    private val binding get() = _binding!!

    val model: FoodViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDirectInputBinding.inflate(layoutInflater)
        val view = binding.root

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Search Dialog")
            .setView(view)
            .setPositiveButton("선택") {
                    dialog, _ ->
                // model 값 넣기
            }
            .setNegativeButton("취소") {
                    dialog, _ ->
                dialog.dismiss()
            }

        initBtn()

        return dialogBuilder.create()
    }

    private fun initBtn() {
        binding.apply {
            editTextSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch()
                    true
                } else {
                    false
                }
            }
            searchBtn.setOnClickListener {
                performSearch()
            }
        }
    }

    private fun performSearch() {
        // model.testset("test")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (activity is ScanInfomation) {
            (activity as ScanInfomation).onDialogDissmissed()
        }
    }
}
