package com.example.funplus.control

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.funplus.R
import com.example.funplus.model.Prize
import com.example.funplus.model.PrizeDB
import com.google.ar.core.AugmentedImage
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.ar_frag.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ArFragMain : Fragment() {
    companion object{
        val imgList = listOf<String>(
            "giftbox.jpg",
            "masha.jpg",
            "minions.jpg",
            "mylittlepony.png",
            "pororo.jpg",
            "surprise.png"
        )
    }

    lateinit var fTransaction: FragmentTransaction
    lateinit var fManager: FragmentManager
    lateinit var prizeListFrag : PrizeListFrag
    lateinit var prizeDB: PrizeDB
    var dbUpdated = false
    private var modelImgMap = mutableMapOf<Int, Pair<ModelRenderable, Int>>()
    private lateinit var fragment: ArFragment
    private lateinit var correctAnswerFrag: CorrectAnswerFrag

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.ar_frag, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fManager = this.fragmentManager!!
        prizeListFrag = PrizeListFrag()
        prizeDB = PrizeDB.get(this.context!!)
        fragment = childFragmentManager.findFragmentById(R.id.arimage_fragment) as ArFragment
        correctAnswerFrag = CorrectAnswerFrag()

        createRenderables()

        fragment.arSceneView.scene.addOnUpdateListener { frameTime ->
            onUpdate(frameTime)
        }

        seePrizeListBtn.setOnClickListener {
            goToPrizeListFrag()
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

        updatedAugmentedImages.forEach {
            when (it.trackingState) {
                TrackingState.TRACKING -> {
                    val anchors = it.anchors
                    if (anchors.isEmpty()) {
                        val imgNode = setupNode(it)

                        Log.d(TAG + "random: ", correctAnswerFrag.numList[0].toString())

                        //val randomNum = activity!!.intent.getIntExtra("randomNum", 0)
                        val randomNum = correctAnswerFrag.randomNum
                        Log.d(TAG + "ramdom: ", randomNum.toString())

                        var modelSet = false
                        /*loop through the lists of images and numbers
                        *make sure the image matches with the image on the displayed number card
                        there are some cards, one side has a number(the number displayed on screen), the other side has an image)
                        */
                        for (index in 0..5) {
                            if (it.name == imgList[index] && randomNum == correctAnswerFrag.numList[index]) {
                                imgNode.renderable = modelImgMap[index]?.first
                                imgNode.scaleController.maxScale = 0.2F
                                imgNode.scaleController.minScale = 0.09f
                                modelSet = true
                                nodeTapListener(imgNode, index)
                                break
                            }
                        }
                        if (!modelSet) {
                            Toast.makeText(this.context, "no matching image", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }


    //create 3d models, and store each model in pair with an image of it
    private fun createRenderables() {
        val uriList = listOf<String>(
            "Birthday Cupcake.sfb",
            "cupcake.sfb",
            "Chocolate Cake.sfb",
            "hamburgeres.sfb",
            "pizza.sfb",
            "diamond.sfb"
        )
        val prizeImgList = listOf<Int>(
            R.drawable.bdcake,
            R.drawable.cupcake,
            R.drawable.chocolate_cake,
            R.drawable.hamburger,
            R.drawable.pizza,
            R.drawable.sugar
        )
        for (i in 0..uriList.lastIndex) {
            val model = ModelRenderable.builder().setSource(this.context, Uri.parse(uriList[i])).build()
            model.thenAccept {
                modelImgMap[i] = Pair(it!!, prizeImgList[i])
            }
        }
    }

    private fun setupNode(it: AugmentedImage): TransformableNode {
        fit_to_scan_img?.visibility = View.GONE
        val pose = it.centerPose
        val anchor = it.createAnchor(pose)
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(fragment.arSceneView.scene)
        val imgNode = TransformableNode(fragment.transformationSystem)
        imgNode.localScale = Vector3(0.4f, 0.4f, 0.4f)
        imgNode.setParent(anchorNode)
        return imgNode
    }

    //when user taps a model, the Prize associated with the model is added to db
    private fun nodeTapListener(imgNode: TransformableNode, index: Int) {
        imgNode.setOnTapListener { hitTestResult, motionEvent ->
            if (!dbUpdated) {
                insertOrUpdatePrizeInDB(imgList[index], modelImgMap[index]!!.second)
            }
            seePrizeListBtn.visibility = View.VISIBLE
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

    private fun goToPrizeListFrag() {
        Log.d(TAG, "goToPrizeListFrag")
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, prizeListFrag)
        fTransaction.commit()
    }
}
