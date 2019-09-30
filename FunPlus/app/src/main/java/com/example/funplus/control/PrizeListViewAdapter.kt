package com.example.funplus.control

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.funplus.R
import com.example.funplus.model.Prize
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_ar.view.*
import kotlinx.android.synthetic.main.prize_row.view.*
import org.jetbrains.anko.image

class PrizeListViewAdapter(var prizeList : List<Prize>, val context: Context) :  RecyclerView.Adapter<PrizeListViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent?.context).inflate(R.layout.prize_row, parent, false)
        return ViewHolder(view);

    }

    override fun getItemCount(): Int {
       return prizeList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.containerView.prizeCountTv.text = prizeList[position].count.toString()
        //holder.containerView.prizeTypeIv.image = prizeList[position].img

        holder.itemView.setOnClickListener {
            // fire recyclerView click event
           // showContactInfo(userList[position].uid)
            Log.d("OnClickListener", "item clicked")
        }
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        /* val txtName = itemView.findViewById<TextView>(R.id.txtName)
         val txtPhone = itemView.findViewById<TextView>(R.id.txtPhone)*/
    }
}