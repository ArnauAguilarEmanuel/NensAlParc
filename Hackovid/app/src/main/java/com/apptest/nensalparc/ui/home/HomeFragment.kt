package com.apptest.nensalparc.ui.home

import android.animation.Animator
import android.animation.LayoutTransition
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.apptest.nensalparc.R
import com.google.android.gms.maps.SupportMapFragment

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.map
import com.google.android.gms.maps.MapView




class HomeFragment : Fragment(), OnMapReadyCallback {


    var mapView: MapView? = null
    private lateinit var mMap: GoogleMap
    var i = 0;

    var latLngs = arrayOf<LatLng>(LatLng(-33.852, 151.211), LatLng(33.852, -151.211), LatLng(-33.852, -151.211))
    override fun onMapReady(googleMap: GoogleMap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            infoDisplay.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING)
            mapContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING)
        }
        mMap = googleMap




        latLngs.forEach {
            mMap.addMarker(
                MarkerOptions().position(it)
                    .title("Marker somewhere")

            )
        }


        mMap.setOnMarkerClickListener{

            if(i%2 == 0)it.title = "Hello";
            else it.title = "by";
            i++


            infoDisplay.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,50f)
            mapContainer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,50f)

            it.showInfoWindow();
            true;
        }

        mMap.setOnMapClickListener {
            infoDisplay.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,0f)
            mapContainer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,100f)
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngs[0]))
    }

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })

        val userPreferences = activity?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val userId = userPreferences?.getString("UserId", "")



        textView.text = userId;
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = map as MapView
        mapView?.onCreate(savedInstanceState)



        mapView?.getMapAsync(this)
    }

    override fun onResume() {
        mapView?.onResume()
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

}