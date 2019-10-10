package com.example.funplus.control

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.funplus.R
import kotlinx.android.synthetic.main.data_activity.*

class DataActivity : AppCompatActivity() {

    private lateinit var numberGameGraphFrag: NumberGraphFrag
    private lateinit var stepCounterFrag: StepCounterFrag
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_activity)

        fManager = supportFragmentManager
        numberGameGraphFrag = NumberGraphFrag()
        stepCounterFrag = StepCounterFrag()
        seeGraphBtn.setBackgroundColor(resources.getColor(R.color.color5Light))
        showNumberGraphFrag()

        backToGameBtn.setOnClickListener{
            goMainActivity()
        }

        seeGraphBtn.setOnClickListener {
            goToFrag(numberGameGraphFrag)
            seeGraphBtn.setBackgroundColor(resources.getColor(R.color.color5Light))
            seeStepCountBtn.setBackgroundColor(resources.getColor(R.color.color5))
        }

        seeStepCountBtn.setOnClickListener {
            goToFrag(stepCounterFrag)
            seeGraphBtn.setBackgroundColor(resources.getColor(R.color.color5))
            seeStepCountBtn.setBackgroundColor(resources.getColor(R.color.color5Light))
        }



    }


    //switch between different fragments
    private fun goToFrag(frag: Fragment) {
        Log.d(TAG, "goToFrag: "+frag)
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.dataFcontainer, frag)
        fTransaction.commit()
    }

    private fun showNumberGraphFrag() {
        Log.d(TAG, "showNumberGraphFrag()")
        fTransaction = fManager.beginTransaction()
        fTransaction.add(R.id.dataFcontainer, numberGameGraphFrag)
        fTransaction.commit()
    }


    private fun goMainActivity(){
        Log.d(TAG, "goToDataActivity")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

}
