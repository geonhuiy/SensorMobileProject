package com.example.funplus

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.correct_answer_frag.*

class CorrectAnswerFrag : Fragment() {
    companion object {

        @JvmStatic
        fun newInstance() =
            CorrectAnswerFrag().apply {
                arguments = Bundle().apply {
                    // putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            // columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.correct_answer_frag, container, false)
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        goToScanBtn.setOnClickListener {
            val intent = Intent(this.context, ArActivity::class.java)
            startActivity(intent)
            Toast.makeText(this.context, "go to ArActivity to scan image", Toast.LENGTH_LONG).show()
           //  getActivity()!!.getSupportFragmentManager().popBackStack()
        }
    }
}