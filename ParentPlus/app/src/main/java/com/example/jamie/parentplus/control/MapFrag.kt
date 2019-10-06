package com.example.jamie.parentplus.control


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jamie.parentplus.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView



class MapFrag : Fragment() {
    lateinit var mapOsmdroid: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.map_frag, container, false)
        mapOsmdroid = view!!.findViewById(R.id.mapOsmdroid) as MapView

        return  view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val latitude= arguments!!.getDouble("latitude")
        val longitude= arguments!!.getDouble("longitude")
        Log.d(TAG+" latitude2: ", latitude.toString())
        Log.d(TAG+" longitude2: ", longitude.toString())
        setMap(latitude, longitude)
    }

    private fun setMap(lat:Double, log:Double) {
        mapOsmdroid.setTileSource(MAPNIK)
        mapOsmdroid.setMultiTouchControls(true)
        mapOsmdroid.controller.setZoom(18.0)
        mapOsmdroid.controller.setCenter(GeoPoint(lat, log))
    }


}
