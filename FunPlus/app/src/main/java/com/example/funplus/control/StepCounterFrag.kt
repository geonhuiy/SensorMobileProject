package com.example.funplus.control

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.funplus.R
import com.example.funplus.model.STEP_COUNT_DATA
import com.example.funplus.model.STEP_COUNT_INTENT
import java.lang.Exception


class StepCounterFrag : Fragment() {
    private lateinit var stepCountTxtView: TextView
    private lateinit var progressBar: ProgressBar
    private var lastStepCount = 0
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.step_counter_frag, container, false)
        stepCountTxtView = view.findViewById(R.id.stepCountTv)
        progressBar = view.findViewById(R.id.progressBar)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this.context!!).registerReceiver(
            broadCastReceiver,
            IntentFilter(STEP_COUNT_INTENT)
        )
        Toast.makeText(this.context, "Waiting for data from sensor...", Toast.LENGTH_LONG).show()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT || newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            try {
                fManager = this.fragmentManager!!
                fTransaction = fManager.beginTransaction()
                fTransaction.detach(this).attach(this).commit()
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * receive data broadcast from sensor
     */
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                STEP_COUNT_INTENT -> {
                    val stepCount = intent.getIntExtra(STEP_COUNT_DATA, 0)
                    updateStepCountUI(stepCount)
                    lastStepCount = stepCount
                }
            }
        }
    }

    /**
     * @param currentSteps : value received from sensor
     * update step count number on UI
     * show a status bar with different indicating colors:
     * from green to red(0 - 10,000 steps), indicating from "active" to "tired"
     */
    private fun updateStepCountUI(currentSteps: Int) {
        stepCountTxtView.text = currentSteps.toString()

        progressBar.progress = currentSteps

        when (currentSteps) {
            in 0..200 -> progressBar.progressTintList = ColorStateList.valueOf(Color.GREEN)
            in 201..499 -> progressBar.progressTintList = ColorStateList.valueOf(Color.YELLOW)
            in 400..799 -> progressBar.progressTintList = ColorStateList.valueOf(Color.MAGENTA)
            in 800..10000 -> progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
        }
    }

}