package com.solverai.gtoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardsAdapter(private val items: List<String>, private val onRemove: (Int)->Unit) :
    RecyclerView.Adapter<CardsAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val txt: TextView = v.findViewById(R.id.cardText)
        val btn: ImageButton = v.findViewById(R.id.removeBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.txt.text = items[position]
        holder.btn.setOnClickListener { onRemove(position) }
    }

    override fun getItemCount(): Int = items.size
}
