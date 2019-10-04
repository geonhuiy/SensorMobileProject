package com.example.funplus.model

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.funplus.control.MainActivity
import com.example.funplus.utility.CAMERA_REQUEST_CODE
import com.example.funplus.utility.PermissionChecker
import com.example.funplus.utility.RECORD_AUDIO_REQUEST_CODE
import java.io.File


class Picture {

    val captureReq = 0
    var photoPath: String = ""

    /**
     * Opens a camera application to take a picture
     * Params are to access activity class functions in a non-activity class
     * @param context Abstract class, handle to Android system
     * @param activity Refers to MainActivity
     */
    fun takePicture(context: Context, activity: MainActivity) {
        //File name and path
        val filename = "sosImg"
        val imgPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var imgFile: File? = null
        imgFile = File.createTempFile(filename, ".jpg", imgPath)
        photoPath = imgFile!!.absolutePath
        val photoUri: Uri = FileProvider.getUriForFile(context, "com.domain.fileprovider", imgFile)

        //ask for permission to use camera if not granted
        PermissionChecker.askForPermissionIfNotGranted(context, activity, CAMERA_REQUEST_CODE, Manifest.permission.CAMERA)

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Starts camera activity
        if (captureIntent.resolveActivity(context.packageManager) != null) {
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            activity.startActivityForResult(captureIntent, captureReq)
        }
    }
}