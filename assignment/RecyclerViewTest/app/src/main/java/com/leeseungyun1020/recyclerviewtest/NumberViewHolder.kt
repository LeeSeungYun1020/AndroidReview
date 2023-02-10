package com.leeseungyun1020.recyclerviewtest

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NumberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        var created = 0
    }

    private val numberTextView: TextView = view.findViewById(R.id.number_text_view)
    private var numberItem: NumberItem? = null

    init {
        Log.d(TAG, "NumberViewHolder: ${++created}")
    }

    fun initItem(item: NumberItem) {
//        if (numberItem != null)
//            return

        numberItem = item
        numberTextView.apply {
            text = item.number.toString()
            setBackgroundColor(item.color)
        }
    }
}