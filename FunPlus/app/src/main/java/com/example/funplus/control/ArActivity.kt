package com.example.funplus.control

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.funplus.R
import com.google.ar.core.AugmentedImage
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_ar.*

class ArActivity : AppCompatActivity() {

    private lateinit var fragment: ArFragment
    private var fitToScanImageView: ImageView? = null
    private var fireRenderable: ModelRenderable? = null
    private var waterRenderable: ModelRenderable? = null
    private var woodRenderable: ModelRenderable? = null
    private var iceRenderable: ModelRenderable? = null
    private var rockRenderable: ModelRenderable? = null
    private var anchorNode : AnchorNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        fragment = supportFragmentManager.findFragmentById(R.id.arimage_fragment) as ArFragment
        fitToScanImageView = findViewById<ImageView>(R.id.fit_to_scan_img)

        val fire = ModelRenderable.builder()
            .setSource(this, Uri.parse("ice.sfb"))
            .build()
        fire.thenAccept { it -> iceRenderable = it }

        fragment.arSceneView.scene.addOnUpdateListener { frameTime ->
            onUpdate(frameTime)
        }

        seePrizeListBtn.setOnClickListener{
            val intent = Intent(this, PrizeListActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "go to prize list activity", Toast.LENGTH_LONG).show()
            finish()
        }

        moreGameBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "go back to main activity", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun onUpdate(frameTime: FrameTime) {
        fragment.onUpdate(frameTime)
        val arFrame = fragment.arSceneView.arFrame
        if (arFrame == null || arFrame.camera.trackingState != TrackingState.TRACKING) {
            return
        }

        val updatedAugmentedImages =
            arFrame.getUpdatedTrackables(AugmentedImage::class.java)

        // Get position of the sun
        updatedAugmentedImages.forEach {
            when (it.trackingState) {

                TrackingState.PAUSED -> { }
                TrackingState.STOPPED -> { }
                TrackingState.TRACKING -> {
                    val anchors = it.anchors
                    if(anchors.isEmpty()) {
                        fitToScanImageView?.visibility = View.GONE
                        val pose = it.centerPose
                        val anchor = it.createAnchor(pose)
                        val anchorNode = AnchorNode(anchor)
                        anchorNode.setParent(fragment.arSceneView.scene)
                        val imgNode = TransformableNode(fragment.transformationSystem)
                        imgNode.setParent(anchorNode)
                        imgNode.setOnTapListener { hitTestResult, motionEvent ->
                            Toast.makeText(this, "tap tap", Toast.LENGTH_LONG).show()
                            val current = prizeCountTv.text.toString().toInt()
                            prizeCountTv.text = (current+1).toString()
                        }
                        if (it.name.equals("ice")) {
                            imgNode.renderable = iceRenderable
                            imgNode.scaleController.maxScale = 0.05f
                            imgNode.scaleController.minScale = 0.009f
                        } else {
                            Toast.makeText(this, "no matching image", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }


}
