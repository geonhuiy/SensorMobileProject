package com.example.funplus.control


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.funplus.R
import com.example.funplus.model.GameDB
import com.example.funplus.model.GameData
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class NumberGraphFrag : Fragment() {
    private lateinit var graph: GraphView
    private lateinit var gameDB: GameDB
    private val points = LineGraphSeries<DataPoint>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.number_graph_frag, container, false)
        graph = view.findViewById(R.id.graph)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        points.resetData(
            Array(0) { DataPoint(-10.0, 0.0) })
        gameDB = GameDB.get(this.context!!)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getAllGamesAndCreatePoints()

    }


    fun getAllGamesAndCreatePoints() {
        doAsync {
            val allGames = gameDB.gameDao().getAll().sortedBy { it.id }
            Log.d(TAG, " GraphGenerator getAllGamesAndCreatePoints allGames:" + allGames.size)
            uiThread {
                for (gameData: GameData in allGames) {
                    val gameId = gameData.id
                    val winCount = gameData.winCount.toDouble()
                    val loseCount = (gameData.loseCount).toDouble()
                    val successRate = if (winCount == 0.0) 0.0 else winCount/(winCount + loseCount)
                    val dataPoint = DataPoint(gameId.toDouble(), successRate)
                    points.appendData(dataPoint, false, 100, true)
                }
                Log.d(TAG, " GraphGenerator().points: " + points)
                setGraph()
            }
        }
    }

    private fun setGraph() {
        graph.addSeries(points)
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true
    }
}
