package com.example.jamie.parentplus.control


import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.jamie.parentplus.R
import kotlinx.android.synthetic.main.photo_frag.*

class PhotoFrag : Fragment() {
    lateinit var photoView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.photo_frag, container, false)
        photoView = view.findViewById<ImageView>(R.id.photoIv)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imgString = arguments!!.getString("imgString")

        try {
            val imageBytes = Base64.decode(imgString, 0)
            val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            photoView.setImageBitmap(imageBitmap)
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
    }


}
