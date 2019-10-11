package com.example.funplus.control

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class ArFrag : ArFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
        Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false
        return view
    }

    override fun getSessionConfiguration(session: Session?): Config {
        val config = super.getSessionConfiguration(session)
        setupImgDb(config, session)
        return config
    }

    /**
     * get image resourses for scanning and add them to AugmentedImageDatabase
     */
    private fun setupImgDb(config: Config, session: Session?) {
        val imageDb = AugmentedImageDatabase(session)
        val assetManager = context!!.assets
        val imgList = ArFragMain.imgList
        for (i in 0..imgList.lastIndex) {
            val inputStream = assetManager.open(imgList[i])
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageDb.addImage(imgList[i], bitmap)
        }
        config.augmentedImageDatabase = imageDb
    }

}