package com.example.funplus.control

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.funplus.R
import com.example.funplus.model.GameModel
import com.example.funplus.model.Prize
import com.example.funplus.model.PrizeDB
import com.example.funplus.model.PrizeModel
import com.google.ar.core.AugmentedImage
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_ar.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ArActivity : AppCompatActivity() {

    lateinit var prizeDB: PrizeDB
    lateinit var correctAnswerFrag: CorrectAnswerFrag
    var dbUpdated = false
    val modelList = mutableListOf<ModelRenderable>()
    val imgList =
        listOf<String>("giftbox", "masha", "minions", "mylittlepony", "pororo", "surprise")

    private lateinit var fragment: ArFragment
    private var fitToScanImageView: ImageView? = null
    private var fireRenderable: ModelRenderable? = null
    private var waterRenderable: ModelRenderable? = null
    private var woodRenderable: ModelRenderable? = null
    private var iceRenderable: ModelRenderable? = null
    private var rockRenderable: ModelRenderable? = null
    private var anchorNode: AnchorNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        correctAnswerFrag = CorrectAnswerFrag()
        prizeDB = PrizeDB.get(this)
        fragment = supportFragmentManager.findFragmentById(R.id.arimage_fragment) as ArFragment
        fitToScanImageView = findViewById<ImageView>(R.id.fit_to_scan_img)


        val ice = ModelRenderable.builder()
            .setSource(this, Uri.parse("ice.sfb"))
            .build()
        ice.thenAccept { it -> iceRenderable = it }

        // modelList.add(iceRenderable!!)

        fragment.arSceneView.scene.addOnUpdateListener { frameTime ->
            onUpdate(frameTime)
        }

        seePrizeListBtn.setOnClickListener {
            val intent = Intent(this, PrizeListActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "go to prize list activity", Toast.LENGTH_LONG).show()
            finish()
        }

        moreGameBtn.setOnClickListener {
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

                TrackingState.PAUSED -> {
                }
                TrackingState.STOPPED -> {
                }
                TrackingState.TRACKING -> {
                    val anchors = it.anchors
                    if (anchors.isEmpty()) {
                        fitToScanImageView?.visibility = View.GONE
                        val pose = it.centerPose
                        val anchor = it.createAnchor(pose)
                        val anchorNode = AnchorNode(anchor)
                        anchorNode.setParent(fragment.arSceneView.scene)
                        val imgNode = TransformableNode(fragment.transformationSystem)
                        imgNode.setParent(anchorNode)

                        Log.d(TAG + "ramdom: ", correctAnswerFrag.numList[0].toString())

                        val randomNum = getIntent().getIntExtra("randomNum", 0)
                        Log.d(TAG + "ramdom: ", randomNum.toString())

                        if (it.name.equals(imgList[0]) && randomNum == correctAnswerFrag.numList[0]) {
                            imgNode.renderable = iceRenderable
                            imgNode.scaleController.maxScale = 0.05f
                            imgNode.scaleController.minScale = 0.009f

                            imgNode.setOnTapListener { hitTestResult, motionEvent ->
                                Toast.makeText(this, "tap tap", Toast.LENGTH_LONG).show()

                                Log.d(TAG + " dbUpdated 1 ", dbUpdated.toString())

                                if (!dbUpdated) {
                                    Log.d(TAG + " dbUpdated 2 ", dbUpdated.toString())
                                    insertOrUpdatePrizeInDB(imgList[0])
                                }

                            }

                        } else {
                            Toast.makeText(this, "no matching image", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }


    /*check if a prize is stored in DB
     */
    private fun isPrizeInDB(imgName: String): Boolean {
        Log.d(TAG, "isPrizeInDB 1:")
        val allPrizes = prizeDB.prizeDao().getAllPrizes()
        Log.d(TAG, "isPrizeInDB():" + allPrizes.size + " prizes stored in DB")
        var isPrizeInDB = false
        if (allPrizes.count() != 0) {
            for (prize: Prize in allPrizes) {
                if (prize.imgName == imgName) {
                    isPrizeInDB = true
                    break
                }
            }
        }
        Log.d(TAG, "isPrizeInDB 2:" + isPrizeInDB)
        return isPrizeInDB
    }

    private fun insertOrUpdatePrizeInDB(imgName: String) {
        Log.d(TAG, "insertOrUpdatePrizeInDB")
        var prizeCount = 1
        doAsync {
            if (isPrizeInDB(imgName)) {
                Log.d(TAG, "isPrizeInDB: true")
                prizeDB.prizeDao().updatePrizeCount(imgName)
                prizeCount = prizeDB.prizeDao().getPrizeCount(imgName)
            } else {
                Log.d(TAG, "isPrizeInDB: false")
                prizeDB.prizeDao().insert(Prize(0, imgName, 1))
            }
            uiThread {
                prizeCountTv.text = prizeCount.toString()
                Log.d(TAG + " prize count: ", prizeCount.toString())
                prizeCountTv.visibility = View.VISIBLE
            }
            dbUpdated = true
            Log.d(TAG+" dbUpdated 3 ", dbUpdated.toString())
        }
    }
}
