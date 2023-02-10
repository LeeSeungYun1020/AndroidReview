package com.leeseungyun1020.recyclerviewtest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NumberAdapter(private val list: List<NumberItem>) : RecyclerView.Adapter<NumberViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder =
        NumberViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.number_item, parent, false)
        )


    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        val item = list[position]
        holder.initItem(item)
    }

    override fun getItemCount(): Int = list.size

}