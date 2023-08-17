package com.example.goodgun.login

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.goodgun.R

class CustomSpinnerAdapter(context: Context, private var list: List<String>?) :
    BaseAdapter() {
    private val inflater: LayoutInflater

    // 스피너에서 선택된 아이템을 액티비티에서 꺼내오는 메서드
    var item: String? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return list?.size ?: 0
    }

    override fun getItem(position: Int): Any {
        return list!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // 화면에 들어왔을 때 보여지는 텍스트뷰 설정
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_outer_view, parent, false)
        }

        if (list != null) {
            item = list!![position]
            (convertView!!.findViewById<View>(R.id.spinner_outer_text) as TextView).text =
                item
        }
        return convertView!!
    }

    // 클릭 후 나타나는 텍스트뷰 설정
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView =
                inflater.inflate(R.layout.spinner_inner_view, parent, false)
        }
        if (list != null) {
            item = list!![position]
            (convertView!!.findViewById<View>(R.id.spinner_inner_text) as TextView).text = item
        }
        return convertView!!
    }
}
