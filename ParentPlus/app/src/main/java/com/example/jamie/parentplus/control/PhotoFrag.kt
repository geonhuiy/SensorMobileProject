package com.example.jamie.parentplus.control


import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jamie.parentplus.R
import kotlinx.android.synthetic.main.photo_frag.*

class PhotoFrag : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.photo_frag, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imgString = arguments!!.getString("imgString")
        Log.d(TAG+" FragimgStr ", imgString!!)

        try {
            val imageBytes = Base64.decode(imgString, 0)
            val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            Log.d(TAG+"imageBitmap", imageBitmap.toString())
            photoIv.setImageBitmap(imageBitmap)
        } catch (e: Exception) {
           Log.d(TAG, e.message!!)
        }
    }


}
