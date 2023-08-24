
package com.example.goodgun.add_food.direct_add

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goodgun.BuildConfig
import com.example.goodgun.add_food.FoodViewModel
import com.example.goodgun.add_food.ScanInfomation
import com.example.goodgun.databinding.FragmentDirectInputBinding
import com.example.goodgun.openAPI.FoodClient
import com.example.goodgun.openAPI.FoodItem
import com.example.goodgun.openAPI.FoodList
import com.example.goodgun.roomDB.FoodEntity
import com.example.goodgun.util.LoadingDialog
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DirectInputFragment : DialogFragment() {
    private var _binding: FragmentDirectInputBinding? = null
    private val binding get() = _binding!!

    lateinit var dialog: AlertDialog
    val model: FoodViewModel by activityViewModels()

    lateinit var adapter: SearchAdapter

    private lateinit var loadingDialog: Dialog // 로딩창 클래스

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

        dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
        }

        initRecyclerView()
        initBtn()

        loadingDialog = LoadingDialog(requireContext())

        return dialog
    }

    private fun initRecyclerView() {
        binding.searchRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter = SearchAdapter(emptyList())
        adapter.click = object : SearchAdapter.OnItemClickListener {
            override fun onItemClick(data: FoodItem, position: Int) {
                GlobalScope.launch(Dispatchers.IO) {
                    val selectFood = FoodEntity(
                        data.foodName,
                        data.calory.toDouble(),
                        data.carbohydrates.toDouble(),
                        data.sugar.toDouble(),
                        data.protein.toDouble(),
                        data.fat.toDouble(),
                        data.trans_fat.toDouble(),
                        data.saturated_fat.toDouble(),
                        data.cholesterol.toDouble(),
                    )
                    model.setfood(selectFood)
                    withContext(Dispatchers.Main) {
                        adapter.selectedItemPosition = position
                        adapter.notifyDataSetChanged()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
                    }
                }
            }
        }

        binding.searchRecyclerView.adapter = adapter
        checkRecyclerViewIsEmpty()
    }

    private fun checkRecyclerViewIsEmpty() {
        if (adapter.itemCount == 0) {
            binding.emptyTextView.visibility = View.VISIBLE
            binding.searchRecyclerView.visibility = View.GONE
        } else {
            binding.emptyTextView.visibility = View.GONE
            binding.searchRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun initBtn() {
        binding.apply {
            editTextSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    loadingDialog = LoadingDialog(requireContext())
                    loadingDialog.show()
                    performSearch()
                    true
                } else {
                    false
                }
            }
            searchBtn.setOnClickListener {
                loadingDialog.show()
                performSearch()
            }
        }
    }

    private fun performSearch() {
        val searchtext = binding.editTextSearch.text.toString()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
        adapter.selectedItemPosition = -1
        if (searchtext == "") {
            binding.emptyTextView.visibility = View.VISIBLE
            binding.searchRecyclerView.visibility = View.GONE
            loadingDialog.dismiss()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                FoodName(searchtext)
            }
        }
    }

    // open api
    private fun FoodName(name: String) {
        FoodClient.foodService.getFoodName(BuildConfig.KEY_ID, "I2790", "json", name)
            .enqueue(object : Callback<FoodList> {
                override fun onResponse(call: Call<FoodList>, response: Response<FoodList>) {
                    if (response.isSuccessful.not()) {
                        Log.e(ContentValues.TAG, response.toString())
                        return
                    } else {
                        response.body()?.let {
                            val foodDto = response.body()?.list
                            val foodList = foodDto?.food ?: emptyList()
                            adapter.setData(foodList)
                            checkRecyclerViewIsEmpty()
                            loadingDialog.dismiss()
                        }
                    }
                }

                override fun onFailure(call: Call<FoodList>, t: Throwable) {
                    Log.e(ContentValues.TAG, "연결 실패")
                    Log.e(ContentValues.TAG, t.toString())
                }
            })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (activity is ScanInfomation) {
            (activity as ScanInfomation).onDialogDissmissed()
        }
    }
}
