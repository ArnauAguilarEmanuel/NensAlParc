package com.apptest.nensalparc.ui.send

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apptest.nensalparc.*
import com.apptest.nensalparc.adapter.HourAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_reservation.*
import kotlinx.android.synthetic.main.fragment_reservation.image_preview
import kotlinx.android.synthetic.main.fragment_reservation.text_address
import kotlinx.android.synthetic.main.fragment_reservation.text_name
import kotlinx.android.synthetic.main.fragment_send.recyclerview
import kotlinx.android.synthetic.main.fragment_share.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class PlaceActivity: AppCompatActivity() {

    private lateinit var sendViewModel: SendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_reservation)
        initUi()
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)


    }
    private val adapter = HourAdapter()
    val db = FirebaseDatabase.getInstance().reference;

    var hours = ArrayList<HourModel>()
    var peopleHours = mutableMapOf<Float, Int>()
    fun refreshHours(dataSnapshot: DataSnapshot,sessionStart: Int, sessionEnd : Int, sessionDuration : Int, dayId: String ){
        hours = ArrayList<HourModel>()
        peopleHours = mutableMapOf<Float, Int>()
        for (i in sessionStart..(sessionEnd - 1)){
            for (j in 0..(60 / sessionDuration) - 1){
                peopleHours.put(i + ((j*sessionDuration) / 100f), 0)
            }
        }


        for (child in dataSnapshot.child("Reservations").child(dayId).children) {
            peopleHours[child.value.toString().toFloat()] =
                peopleHours[child.value.toString().toFloat()].toString().toInt() + 1
        }
    }

    fun displayData(dataSnapshot: DataSnapshot,sessionStart: Int, sessionEnd : Int, sessionDuration : Int, maxCapacity: Int ){
        for (i in sessionStart..(sessionEnd - 1)) {

            hours.add(HourModel(i, ArrayList<TimeFractionModel>()))

            for (j in 0..(60 / sessionDuration) - 1) {
                hours[i - sessionStart].timeFractions?.add(
                    TimeFractionModel(
                        j * sessionDuration,
                        sessionDuration,
                        maxCapacity,
                        peopleHours[i + ((j*sessionDuration) / 100f)]
                    )
                )
            }

        }

        adapter.elements = hours
        adapter.notifyDataSetChanged()
    }
    var dayId = ""
    var sYear = 0
    var sMonth = 0
    var sDay = 0
    var dataListener = object :
            ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var sessionEnd = dataSnapshot.child("End").value.toString().toInt()
            var sessionStart = dataSnapshot.child("Start").value.toString().toInt()
            var sessionDuration =
                dataSnapshot.child("SessionDuration").value.toString().toInt()
            var maxCapacity = dataSnapshot.child("MaxCapacity").value.toString().toInt()
            val c = Calendar.getInstance()
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
            var day = c.get(Calendar.DAY_OF_MONTH)
            if(sYear!=0){
                year = sYear
                month = sMonth
                day = sDay
            }


            dayId = day.toString() + "-" + (month + 1) + "-" + year

            refreshHours(dataSnapshot, sessionStart, sessionEnd, sessionDuration, dayId)

            displayData(dataSnapshot, sessionStart, sessionEnd, sessionDuration, maxCapacity)


        }


        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    fun initUi(){

        var context = this
        var place = context.intent.getSerializableExtra("place") as AreaInfoModel
        text_address.text = place.address;
        text_name.text = place.name;
        Picasso.get().load(place.imageUrl).into(image_preview);
        val userPreferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val userId = userPreferences?.getString("UserId", "")
        val UserName = userPreferences?.getString("UserName", "")
        val UserDNI = userPreferences?.getString("UserDNI", "")
        val UserLocation = userPreferences?.getString("UserLocation", "")

        var user = User(UserName, UserLocation, UserDNI, userId);

        lifecycleScope.launch{
            try {

                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)



                button_select_day.text = day.toString() + "/" + (month + 1) + "/" + year


                button_select_day.setOnClickListener {
                    val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener{ view, mYear, mMonth, mDay ->
                        button_select_day.text = mDay.toString() + "/" + (mMonth + 1) + "/" + mYear
                        sYear = mYear
                        sMonth = mMonth
                        sDay = mDay
                        db.child("Locations").child(user.location.toString()).child("Places").child(place.placeID.toString()).child("SessionData").addListenerForSingleValueEvent(dataListener)
                    }, year, month, day)

                    dpd.show()
                }


                recyclerview.adapter = adapter
                recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


                db.child("Locations").child(user.location.toString()).child("Places").child(place.placeID.toString()).child("SessionData").addValueEventListener(dataListener)



            } catch (e: IOException){
                //No Internet or Server down
                Log.w("StreamsFragment", "Request couldn't be executed")
            }
        }
    }
}