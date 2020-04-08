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

import androidx.appcompat.app.AppCompatActivity;
import com.apptest.nensalparc.AreaInfoModel
import com.apptest.nensalparc.User
import com.apptest.nensalparc.ui.share.ShareFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            infoDisplay.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING)
            mapContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING)
        }
        mMap = googleMap

        db.child("Locations").child(user.location.toString()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.child("Places").children.forEach() {
                    var place = AreaInfoModel(it.child("Name").value.toString(),it.child("Address").value.toString(),it.child("imageURL").value.toString(), it.key.toString())
                    places.put(place.name.toString(),place)
                    mMap.addMarker(
                        MarkerOptions().position(
                            LatLng(it.child("LatitudeLongitude").child("Lat").value.toString().toDouble(),
                                it.child("LatitudeLongitude").child("Long").value.toString().toDouble()
                            )
                        ).title(it.child("Name").value.toString())

                    )
                }

                mMap.setOnMarkerClickListener{


                    var ft = fragmentManager?.beginTransaction();

                    //get the actual city
                    var place = places[it.title]!!
                    ft?.replace(R.id.infoDisplay, ShareFragment(place))
                    ft?.commit();


                    infoDisplay.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,50f)
                    mapContainer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,50f)

                    it.showInfoWindow();
                    true;
                }

                mMap.setOnMapClickListener {
                    infoDisplay.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,0f)
                    mapContainer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,100f)
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(
                    dataSnapshot.child("LatitudeLongitude").child("Lat").value as Double,
                    dataSnapshot.child("LatitudeLongitude").child("Long").value as Double

                )))

                mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)

            }
        });





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
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })

        val userPreferences = activity?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val userId = userPreferences?.getString("UserId", "")
        val UserName = userPreferences?.getString("UserName", "")
        val UserDNI = userPreferences?.getString("UserDNI", "")
        val UserLocation = userPreferences?.getString("UserLocation", "")

        user = User(UserName, UserLocation, UserDNI, userId);



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