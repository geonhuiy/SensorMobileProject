package com.example.funplus

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class ArFrag: ArFragment() {
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
        setupAugmentedImageDatabase(config, session)
        return config
    }

    private fun setupAugmentedImageDatabase(config: Config, session: Session?) {
        val augmentedImageDb = AugmentedImageDatabase(session)
        val assetManager = context!!.assets

//        val fire = assetManager.open("fire.png")
//        val augmentedImageBitmap1 = BitmapFactory.decodeStream(fire)
//        augmentedImageDb.addImage("fire", augmentedImageBitmap1)
//
//        val water = assetManager.open("drop.png")
//        val augmentedImageBitmap2 = BitmapFactory.decodeStream(water)
//        augmentedImageDb.addImage("water", augmentedImageBitmap2)
//
//        val wood = assetManager.open("tree.png")
//        val augmentedImageBitmap3 = BitmapFactory.decodeStream(wood)
//        augmentedImageDb.addImage("wood", augmentedImageBitmap3)

        val ice = assetManager.open("ice.png")
        val augmentedImageBitmap4 = BitmapFactory.decodeStream(ice)
        augmentedImageDb.addImage("ice", augmentedImageBitmap4)

//        val rock = assetManager.open("mountain.png")
//        val augmentedImageBitmap5 = BitmapFactory.decodeStream(rock)
//        augmentedImageDb.addImage("rock", augmentedImageBitmap5)
//
//        val sun = assetManager.open("sun.jpg")
//        val augmentedImageBitmap6 = BitmapFactory.decodeStream(sun)
//        augmentedImageDb.addImage("sun", augmentedImageBitmap6)

        config.augmentedImageDatabase = augmentedImageDb
    }

}