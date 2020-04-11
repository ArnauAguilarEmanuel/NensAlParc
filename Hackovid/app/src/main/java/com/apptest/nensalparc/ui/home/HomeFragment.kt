package com.apptest.nensalparc.ui.home

import android.Manifest
import android.animation.Animator
import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.apptest.nensalparc.R

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.apptest.nensalparc.AreaInfoModel
import com.apptest.nensalparc.MainActivity
import com.apptest.nensalparc.User
import com.apptest.nensalparc.ui.SignInActivity
import com.apptest.nensalparc.ui.share.PreviewFragmentAdmin
import com.apptest.nensalparc.ui.share.ShareFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.map
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.nav_header_main.*


class HomeFragment: Fragment(), OnMapReadyCallback {


    var mapView: MapView? = null
    private lateinit var mMap: GoogleMap
    var i = 0;
    var places = mutableMapOf<String, AreaInfoModel>()

    var latLngs = arrayOf<LatLng>(LatLng(-33.852, 151.211), LatLng(33.852, -151.211), LatLng(-33.852, -151.211))
    override fun onMapReady(googleMap: GoogleMap) {
        if(user.uId == "") return;
        mMap = googleMap


        var mode = context?.resources?.configuration?.uiMode
        var nightModeFlags = Configuration.UI_MODE_NIGHT_MASK
        if(mode != null)
          nightModeFlags = mode and Configuration.UI_MODE_NIGHT_MASK;

        if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.requireContext(), R.raw.style_dark));
        }else{
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.requireContext(), R.raw.style_json));
        }


        if (ContextCompat.checkSelfPermission(this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_CALENDAR)
            ActivityCompat.requestPermissions(this.requireActivity(), permissions,0)

        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                infoDisplay.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING)
                mapContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING)
            }

            mMap.setMyLocationEnabled(true)

            db.child("Locations").child(user.location.toString()).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.child("Places").children.forEach() {
                        var place = AreaInfoModel(it.child("Name").value.toString(), it.child("Address").value.toString(), it.child("imageURL").value.toString(), it.key.toString())
                        places.put(place.name.toString(), place)
                        mMap.addMarker(
                            MarkerOptions().position(
                                LatLng(it.child("LatitudeLongitude").child("Lat").value.toString().toDouble(),
                                    it.child("LatitudeLongitude").child("Long").value.toString().toDouble()
                                )
                            ).title(it.child("Name").value.toString())

                        )
                    }

                    mMap.setOnMarkerClickListener {


                        var ft = fragmentManager?.beginTransaction();

                        //get the actual city
                        var place = places[it.title]!!
                        db.child("Users").child(user.uId.toString()).child("admin").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (!dataSnapshot.exists())
                                    ft?.replace(R.id.infoDisplay, ShareFragment(place))
                                else
                                    ft?.replace(R.id.infoDisplay, PreviewFragmentAdmin(place))

                                ft?.commit();


                                infoDisplay.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 50f)
                                mapContainer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 50f)

                                it.showInfoWindow();
                            }

                        })

                        true;
                    }

                    mMap.setOnMapClickListener {
                        infoDisplay.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0f)
                        mapContainer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 100f)
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(
                        dataSnapshot.child("LatitudeLongitude").child("Lat").value as Double,
                        dataSnapshot.child("LatitudeLongitude").child("Long").value as Double

                    )))

                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)

                }
            });
        }



    }



    private lateinit var homeViewModel: HomeViewModel
    lateinit var user : User;
    val db = FirebaseDatabase.getInstance().reference;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val userPreferences = activity?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val userId = userPreferences?.getString("UserId", "")
        val UserName = userPreferences?.getString("UserName", "")
        val UserDNI = userPreferences?.getString("UserDNI", "")
        val UserLocation = userPreferences?.getString("UserLocation", "")

        user = User(UserName, UserLocation, UserDNI, userId);



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