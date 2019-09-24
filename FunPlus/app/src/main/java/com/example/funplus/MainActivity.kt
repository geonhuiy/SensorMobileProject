package com.example.funplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "DBG"
class MainActivity : AppCompatActivity() {
    lateinit var plusMinusFrag: PlusMinusFrag
    lateinit var letterFrag: LetterFrag
    lateinit var sosFrag: SosFrag
    lateinit var fTransaction: FragmentTransaction
    lateinit var fManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fManager = supportFragmentManager
        showPlusMinusFrag()
        plusMinusFrag = PlusMinusFrag()
        letterFrag = LetterFrag()
        sosFrag = SosFrag()

        goToNumberGameBtn.setOnClickListener{
            goToGameFrag(plusMinusFrag)
        }
        goToLetterGameBtn.setOnClickListener{
            goToGameFrag(letterFrag)
        }
        sosBtn.setOnClickListener {
            goToGameFrag(sosFrag)
        }
    }


    private fun showPlusMinusFrag() {
        Log.d(TAG, "showPlusMinusFrag()")
        plusMinusFrag = PlusMinusFrag()
        fTransaction = fManager.beginTransaction()
        fTransaction.add(R.id.fcontainer, plusMinusFrag)
        fTransaction.commit()
    }

    private fun goToGameFrag(frag: Fragment) {
        Log.d(TAG, "goToGameFrag")
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        //fTransaction.addToBackStack(null)
        fTransaction.commit()
    }

}
