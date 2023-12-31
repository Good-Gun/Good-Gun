package com.example.goodgun.add_food.direct_add

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goodgun.BuildConfig
import com.example.goodgun.add_food.FoodViewModel
import com.example.goodgun.add_food.ScanInfomation
import com.example.goodgun.databinding.FragmentDirectInputBinding
import com.example.goodgun.openAPI.FoodClient
import com.example.goodgun.openAPI.FoodItem
import com.example.goodgun.openAPI.FoodList
import com.example.goodgun.roomDB.DatabaseManager
import com.example.goodgun.roomDB.FoodDatabase
import com.example.goodgun.roomDB.FoodEntity
import com.example.goodgun.util.LoadingDialog
import com.example.goodgun.util.ScreenUtil
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DirectInputFragment : DialogFragment() {
    private var _binding: FragmentDirectInputBinding? = null
    private val binding get() = _binding!!

    val model: FoodViewModel by activityViewModels()

    lateinit var adapter: SearchAdapter

    private lateinit var loadingDialog: Dialog // 로딩창 클래스

    private lateinit var roomdb: FoodDatabase

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDirectInputBinding.inflate(layoutInflater)
        val view = binding.root
        val layoutParams = WindowManager.LayoutParams()
        val screenSize = ScreenUtil.getScreenSize(requireActivity())
        val screenWidth = screenSize.first
        val screenHeight = screenSize.second
        val screenDensity = ScreenUtil.getScreenDensity(requireActivity())
        val dialog = AlertDialog.Builder(requireActivity())
            .setView(view)
            .create()

        initRoomDB()

        layoutParams.copyFrom(dialog!!.window?.attributes)
        layoutParams.width = screenWidth - 200 // 원하는 너비
        layoutParams.height = dpToPx(requireActivity(), 160)
        dialog.window?.attributes = layoutParams

        initRecyclerView()
        initBtn()

        loadingDialog = LoadingDialog(requireContext())

        return dialog
    }

    private fun initRoomDB() {
        val uid = model.getuserid()
        if (uid == "") {
            Toast.makeText(requireContext(), "유효하지 않은 유저입니다.", Toast.LENGTH_SHORT).show()
        }
        roomdb = DatabaseManager.getDatabaseInstance(uid, requireContext())
    }

    private fun initRecyclerView() {
        binding.searchRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.searchRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                LinearLayoutManager.VERTICAL,
            ),
        )
        adapter = SearchAdapter(emptyList())
        adapter.click = object : SearchAdapter.OnItemClickListener {
            override fun onItemClick(data: FoodItem, position: Int) {
                GlobalScope.launch(Dispatchers.IO) {
                    val selectFood = FoodEntity(
                        data.foodName,
                        data.calory?.toDoubleOrNull() ?: 0.0,
                        data.carbohydrates?.toDoubleOrNull() ?: 0.0,
                        data.sugar?.toDoubleOrNull() ?: 0.0,
                        data.protein?.toDoubleOrNull() ?: 0.0,
                        data.fat?.toDoubleOrNull() ?: 0.0,
                        data.trans_fat?.toDoubleOrNull() ?: 0.0,
                        data.saturated_fat?.toDoubleOrNull() ?: 0.0,
                        data.cholesterol?.toDoubleOrNull() ?: 0.0,
                        data.sodium?.toDoubleOrNull() ?: 0.0,
                        true,
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        roomdb.foodDao().saveFood(selectFood)
                    }
                    model.setfood(selectFood)
                    withContext(Dispatchers.Main) {
                        adapter.selectedItemPosition = position
                        adapter.notifyDataSetChanged()
//                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
                        dialog!!.dismiss()
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
            backBtn.setOnClickListener {
                dialog!!.dismiss()
            }
        }
    }

    private fun performSearch() {
        val searchtext = binding.editTextSearch.text.toString()
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
        adapter.selectedItemPosition = -1
        if (searchtext == "") {
            binding.emptyTextView.visibility = View.VISIBLE
            binding.searchRecyclerView.visibility = View.GONE
            loadingDialog.dismiss()
            resizeDialog(160)
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
                            resizeDialog(if (foodList.isNotEmpty()) 600 else 160)
                        }
                    }
                }

                override fun onFailure(call: Call<FoodList>, t: Throwable) {
                    Log.e(ContentValues.TAG, "연결 실패")
                    Log.e(ContentValues.TAG, t.toString())
                }
            })
    }

    private fun resizeDialog(dp: Int) {
        val window = dialog!!.window
        val layoutParams = window?.attributes

        val totalItemHeight = binding.searchRecyclerView.height ?: 0
        Log.i("height", totalItemHeight.toString())
        // 원하는 추가 여백을 고려하여 Dialog 크기 설정
        val desiredDialogHeight = /* additional padding */ dpToPx(requireContext(), dp)

        layoutParams?.apply {
            height = desiredDialogHeight
        }

        window?.attributes = layoutParams
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (activity is ScanInfomation) {
            (activity as ScanInfomation).onDialogDissmissed()
        }
    }
}
