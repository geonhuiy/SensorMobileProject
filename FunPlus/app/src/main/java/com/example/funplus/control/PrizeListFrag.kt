package com.example.funplus.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.funplus.R
import com.example.funplus.model.PrizeDB
import kotlinx.android.synthetic.main.prize_list_frag.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PrizeListFrag : Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.prize_list_frag, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /**
         * get prizes from db
         * display on UI
         */
        val db = PrizeDB.get(this.requireContext())
        doAsync {
            val prizeList = db.prizeDao().getAllPrizes()
            uiThread {
                recyclerView.adapter = PrizeListViewAdapter(prizeList)
            }
        }
    }
}