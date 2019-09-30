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

        val giftbox = assetManager.open("giftbox.jpg")
        val augmentedImageBitmap1 = BitmapFactory.decodeStream(giftbox)
        augmentedImageDb.addImage("giftbox", augmentedImageBitmap1)

        val masha = assetManager.open("masha.jpg")
        val augmentedImageBitmap2 = BitmapFactory.decodeStream(masha)
        augmentedImageDb.addImage("masha", augmentedImageBitmap2)

        val minions = assetManager.open("minions.jpg")
        val augmentedImageBitmap3 = BitmapFactory.decodeStream(minions)
        augmentedImageDb.addImage("minions", augmentedImageBitmap3)

        val ice = assetManager.open("ice.png")
        val augmentedImageBitmap4 = BitmapFactory.decodeStream(ice)
        augmentedImageDb.addImage("ice", augmentedImageBitmap4)

        val mylittlepony = assetManager.open("mylittlepony.png")
        val augmentedImageBitmap5 = BitmapFactory.decodeStream(mylittlepony)
        augmentedImageDb.addImage("mylittlepony", augmentedImageBitmap5)

        val pororo = assetManager.open("pororo.jpg")
        val augmentedImageBitmap6 = BitmapFactory.decodeStream(pororo)
        augmentedImageDb.addImage("pororo", augmentedImageBitmap6)

        val surprise = assetManager.open("surprise.png")
        val augmentedImageBitmap7 = BitmapFactory.decodeStream(surprise)
        augmentedImageDb.addImage("surprise", augmentedImageBitmap7)

        config.augmentedImageDatabase = augmentedImageDb
    }

}