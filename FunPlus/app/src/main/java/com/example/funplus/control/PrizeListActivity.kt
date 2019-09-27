package com.example.funplus.control

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.funplus.R
import kotlinx.android.synthetic.main.activity_prize_list.*

class PrizeListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prize_list)

        backToGameBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "prize list activity: go back to main activity", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
