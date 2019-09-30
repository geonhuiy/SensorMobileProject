package com.example.funplus.control

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.funplus.R
import com.example.funplus.model.Prize
import com.example.funplus.model.PrizeDB
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
    val scanImgList =
        listOf<String>("giftbox", "masha", "minions", "mylittlepony", "pororo", "surprise")
    val prizeImgList = listOf<Int>(
        R.drawable.bdcake,
        R.drawable.cupcake,
        R.drawable.chocolate_cake,
        R.drawable.hamburger,
        R.drawable.pizza,
        R.drawable.sugar
    )
    val modelList = mutableListOf<ModelRenderable>()
    private lateinit var fragment: ArFragment
    private var fitToScanImageView: ImageView? = null
    private var bdCupcakeRenderable: ModelRenderable? = null
    private var chocolateRenderable: ModelRenderable? = null
    private var cupcakeRenderable: ModelRenderable? = null
    private var iceRenderable: ModelRenderable? = null
    private var hamburgerRenderable: ModelRenderable? = null
    private var pizzaRenderable: ModelRenderable? = null
    private var anchorNode: AnchorNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        correctAnswerFrag = CorrectAnswerFrag()
        prizeDB = PrizeDB.get(this)
        fragment = supportFragmentManager.findFragmentById(R.id.arimage_fragment) as ArFragment
        fitToScanImageView = findViewById<ImageView>(R.id.fit_to_scan_img)

        createRenderables()

        fragment.arSceneView.scene.addOnUpdateListener { frameTime ->
            onUpdate(frameTime)
        }

        seePrizeListBtn.setOnClickListener {
            goToPrizeList()
        }

        moreGameBtn.setOnClickListener {
            goToPlusMinus()
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
                TrackingState.TRACKING -> {
                    val anchors = it.anchors
                    if (anchors.isEmpty()) {
                        val imgNode = setUpNode(it)

                        Log.d(TAG + "ramdom: ", correctAnswerFrag.numList[0].toString())

                        val randomNum = getIntent().getIntExtra("randomNum", 0)
                        Log.d(TAG + "ramdom: ", randomNum.toString())

                        var modelSet = false
                        //loop through the lists of images and numbers
                        //make sure the image matches with the number displayed
                        //there are some cards, one side has a number(the number displayed on screen), the other has an image)
                        for (index in 0..5) {
                            if (it.name.equals(scanImgList[index]) && randomNum == correctAnswerFrag.numList[index]) {
                                imgNode.renderable = modelList[index]
                                imgNode.scaleController.maxScale = 0.05f
                                imgNode.scaleController.minScale = 0.009f
                                modelSet = true
                                nodeTapListener(imgNode, index)
                                break
                            }
                        }
                        if (!modelSet) {
                            Toast.makeText(this, "no matching image", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun createRenderables() {
        val bdCake = ModelRenderable.builder()
            .setSource(this, Uri.parse("Birthday Cupcake.sfb"))
            .build()
        bdCake.thenAccept { it ->
            bdCupcakeRenderable = it
            modelList.add(bdCupcakeRenderable!!)
        }

        val chocolateCake = ModelRenderable.builder()
            .setSource(this, Uri.parse("Chocolate Cake.sfb"))
            .build()
        chocolateCake.thenAccept { it ->
            chocolateRenderable = it
            modelList.add(chocolateRenderable!!)
        }

        val cupcake = ModelRenderable.builder()
            .setSource(this, Uri.parse("cupcake.sfb"))
            .build()
        cupcake.thenAccept { it ->
            cupcakeRenderable = it
            modelList.add(cupcakeRenderable!!)
        }

        val hamberger = ModelRenderable.builder()
            .setSource(this, Uri.parse("hamburgeres.sfb"))
            .build()
        hamberger.thenAccept { it ->
            hamburgerRenderable = it
            modelList.add(hamburgerRenderable!!)
        }

        val pizza = ModelRenderable.builder()
            .setSource(this, Uri.parse("pizza.sfb"))
            .build()
        pizza.thenAccept { it ->
            pizzaRenderable = it
            modelList.add(pizzaRenderable!!)
        }

        val ice = ModelRenderable.builder()
            .setSource(this, Uri.parse("ice.sfb"))
            .build()
        ice.thenAccept { it ->
            iceRenderable = it
            modelList.add(iceRenderable!!)
        }
    }

    private fun setUpNode(it: AugmentedImage): TransformableNode {
        fitToScanImageView?.visibility = View.GONE
        val pose = it.centerPose
        val anchor = it.createAnchor(pose)
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(fragment.arSceneView.scene)
        val imgNode = TransformableNode(fragment.transformationSystem)
        imgNode.setParent(anchorNode)
        return imgNode
    }

    //when user taps a model, the Prize associated with the model is added to db
    private fun nodeTapListener(imgNode: TransformableNode, index: Int) {
        imgNode.setOnTapListener { hitTestResult, motionEvent ->
            if (!dbUpdated) {
                insertOrUpdatePrizeInDB(scanImgList[index], prizeImgList[index])
            }

        }
    }


    /*check if a prize is stored in DB
     */
    private fun isPrizeInDB(imgToScan: String): Boolean {
        Log.d(TAG, "isPrizeInDB 1:")
        val allPrizes = prizeDB.prizeDao().getAllPrizes()
        Log.d(TAG, "isPrizeInDB():" + allPrizes.size + " prizes stored in DB")
        var isPrizeInDB = false
        if (allPrizes.count() != 0) {
            for (prize: Prize in allPrizes) {
                if (prize.imgToScan == imgToScan) {
                    isPrizeInDB = true
                    break
                }
            }
        }
        Log.d(TAG, "isPrizeInDB 2:" + isPrizeInDB)
        return isPrizeInDB
    }

    /*if a Prize is already in db, update count
       it not, insert into db
    */
    private fun insertOrUpdatePrizeInDB(imgToScan: String, prizeImg: Int) {
        Log.d(TAG, "insertOrUpdatePrizeInDB")
        var prizeCount = 1
        doAsync {
            if (isPrizeInDB(imgToScan)) {
                Log.d(TAG, "isPrizeInDB: true")
                prizeDB.prizeDao().updatePrizeCount(imgToScan)
                prizeCount = prizeDB.prizeDao().getPrizeCount(imgToScan)
            } else {
                Log.d(TAG, "isPrizeInDB: false")
                prizeDB.prizeDao().insert(Prize(0, imgToScan, prizeImg, 1))
            }
            uiThread {
                prizeCountTv.text = prizeCount.toString()
                Log.d(TAG + " prize count: ", prizeCount.toString())
                prizeCountTv.visibility = View.VISIBLE
            }
            dbUpdated = true
        }
    }


    private fun goToPlusMinus() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "go back to main activity", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun goToPrizeList() {
        val intent = Intent(this, PrizeListActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "go to see prize list", Toast.LENGTH_SHORT).show()
        finish()
    }
}
