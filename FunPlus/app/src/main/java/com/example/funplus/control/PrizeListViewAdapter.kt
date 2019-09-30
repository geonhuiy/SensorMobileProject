package com.example.funplus.control

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.funplus.R
import com.example.funplus.model.Prize
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_ar.view.*
import kotlinx.android.synthetic.main.prize_row.view.*
import org.jetbrains.anko.image

class PrizeListViewAdapter(var prizeList : List<Prize>) :  RecyclerView.Adapter<PrizeListViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent?.context).inflate(R.layout.prize_row, parent, false)
        return ViewHolder(view);

    }

    override fun getItemCount(): Int {
       return prizeList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.prizeCountTv.text = prizeList[position].count.toString()
        holder.prizeImgIv.setImageResource(prizeList[position].prizeImg)

        holder.itemView.setOnClickListener {
            // fire recyclerView click event
           // showContactInfo(userList[position].uid)
            Log.d("OnClickListener", "item clicked")
        }
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        val prizeCountTv = itemView.findViewById<TextView>(R.id.singlePrizeCountTv)
        val prizeImgIv = itemView.findViewById<ImageView>(R.id.prizeImgIv)
    }
}