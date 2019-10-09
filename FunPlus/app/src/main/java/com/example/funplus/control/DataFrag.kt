package com.example.funplus.control


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.example.funplus.R
import com.example.funplus.model.STEP_COUNT_DATA
import com.example.funplus.model.STEP_COUNT_INTENT
import kotlinx.android.synthetic.main.data_frag.*

class DataFrag : Fragment() {
    private var currentSteps = 0
    private lateinit var stepCountTxtView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.data_frag, container, false)
        stepCountTxtView = view.findViewById(R.id.stepCountTv)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this.context!!).registerReceiver(
            broadCastReceiver,
            IntentFilter(STEP_COUNT_INTENT)
        )

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

       // updateStepCountUI()

    }


    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                STEP_COUNT_INTENT -> {
                    Log.d(TAG, "data frag, broadCastReceiver(), new step count data")
                    val stepCount = intent.getIntExtra(STEP_COUNT_DATA, 0)
                    Log.d(TAG, "new step count is " + stepCount)
                    updateStepCountUI(stepCount)
                }
            }
        }
    }

    private fun updateStepCountUI(currentSteps: Int) {
        Log.d(TAG, "updateStepCountUI")
        Log.d(TAG, "updateStepCountUI currentSteps=" +currentSteps)
        stepCountTxtView.text = currentSteps.toString()

        if (currentSteps <= progressBar.max) {
            progressBar.setProgress(currentSteps)
        }
        if (currentSteps in 0..200) {
            progressBar.progressDrawable.setColorFilter(
                Color.GREEN, PorterDuff.Mode.SRC_IN
            )
        } else if (currentSteps in 201..499) {
            progressBar.progressDrawable.setColorFilter(
                Color.YELLOW, PorterDuff.Mode.SRC_IN
            )
        } else if (currentSteps in 400..799) {
            progressBar.progressDrawable.setColorFilter(
                Color.MAGENTA, PorterDuff.Mode.SRC_IN
            )
        } else if (currentSteps in 800..10000) {
            progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
        }
    }

}
