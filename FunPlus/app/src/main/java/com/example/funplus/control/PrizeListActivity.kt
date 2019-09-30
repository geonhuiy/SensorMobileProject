package com.example.funplus.control

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.funplus.R
import com.example.funplus.model.PrizeDB
import kotlinx.android.synthetic.main.activity_prize_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PrizeListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prize_list)

        /**
         * get prizes from db
         * display on UI
         */
        val db = PrizeDB.get(this)
        doAsync {
            val prizeList = db.prizeDao().getAllPrizes()
            uiThread {
                recyclerView.adapter = PrizeListViewAdapter(prizeList)
            }
        }

        backToGameBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "from prize list activity: back to main activity", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
