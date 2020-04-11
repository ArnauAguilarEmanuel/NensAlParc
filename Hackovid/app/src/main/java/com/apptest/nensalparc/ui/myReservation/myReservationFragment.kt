package com.apptest.nensalparc.ui.myReservation

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.apptest.nensalparc.R
import com.apptest.nensalparc.User
import com.apptest.nensalparc.adapter.HourAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_my_reservation.*
import kotlinx.android.synthetic.main.fragment_my_reservation.image_preview
import kotlinx.android.synthetic.main.fragment_share.*
import java.util.*

fun Float.round(decimals: Int): Float {
    var multiplier = 1f
    repeat(decimals) { multiplier *= 10f }
    return kotlin.math.round(this * multiplier) / multiplier
}

class myReservationFragment : Fragment(){

    val db = FirebaseDatabase.getInstance().reference;

    fun parseDate(stringDate : String, separator: String): HourAdapter.DateHolder {
        var date = HourAdapter.DateHolder(0, 0, 0)

        val parts = stringDate.split(separator)

        if(parts.size>0)
            date.day = parts[0].toInt()
        if(parts.size>1)
            date.month = parts[1].toInt()
        if(parts.size>2)
            date.year = parts[2].toInt()
        return date
    }

    fun datePassed(reservedDate: HourAdapter.DateHolder, reservedHour: HourAdapter.DateHolder, sessionDuration: Int):Boolean{
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        var nowDate = HourAdapter.DateHolder(day, month + 1, year)
        var nowHour = HourAdapter.DateHolder(hour, minute, 0)

        return (getElapsedTime(reservedDate, reservedHour) + sessionDuration) < getElapsedTime(nowDate, nowHour)
    }

    fun getElapsedTime(reservedDate: HourAdapter.DateHolder, reservedHour: HourAdapter.DateHolder):Double{
        var time = 0.0;
        time += (reservedDate.year -2020) *     525600.0
        time += (reservedDate.month-1) *        43800.0
        time += (reservedDate.day-1) *          1440.0
        time += (reservedHour.day) *            60.0//hour
        time += (reservedHour.month) *          1.0//minutes

        return time;

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_my_reservation, container, false)



        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userPreferences = activity?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val userId = userPreferences?.getString("UserId", "")
        val UserName = userPreferences?.getString("UserName", "")
        val UserDNI = userPreferences?.getString("UserDNI", "")
        val UserLocation = userPreferences?.getString("UserLocation", "")

        var user = User(UserName, UserLocation, UserDNI, userId);
        db.child("Users").child(user.uId.toString()).child("Reservation").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    my_reservation_cancel.visibility = View.VISIBLE
                    text_reservation_address.visibility = View.VISIBLE
                    text_reservation_date.visibility = View.VISIBLE
                    reservaPerElDia.visibility = View.VISIBLE
                    text_reservation_hour.visibility = View.VISIBLE
                    text_reservation_hour_end.visibility = View.VISIBLE
                    text_reservation_request.visibility = View.VISIBLE
                    durant.visibility = View.VISIBLE

                    my_reservation_cancel.setOnClickListener({
                        db.child("Locations").child(user.location.toString()).child("Places").child(dataSnapshot.child("placeID").value.toString()).child("SessionData")
                            .child("Reservations").child(dataSnapshot.child("reservedDate").value.toString()).child(user.uId.toString()).removeValue()

                        db.child("Users").child(user.uId.toString()).child("Reservation").removeValue()
                        my_reservation_cancel.visibility = View.GONE
                        text_reservation_address.visibility = View.GONE
                        text_reservation_date.visibility = View.GONE
                        reservaPerElDia.visibility = View.GONE
                        text_reservation_hour.visibility = View.GONE
                        text_reservation_hour_end.visibility = View.GONE
                        text_reservation_request.visibility = View.GONE
                        durant.visibility = View.GONE
                        text_reservation_name.text = "Reserva Cancelada"
                    })

                    var reservedDate = parseDate(dataSnapshot.child("reservedDate").value.toString(),"-")
                    var reservedHour = parseDate(dataSnapshot.child( "reservedHour").value.toString(),".")
                    if(datePassed(reservedDate, reservedHour, dataSnapshot.child(("duration")).value.toString().toInt())){
                        db.child("Users").child(user.uId.toString()).child("Reservation").removeValue()
                    }else{
                        text_reservation_name.text = dataSnapshot.child("name").value.toString()
                        text_reservation_address.text = dataSnapshot.child("address").value.toString()
                        text_reservation_date.text = dataSnapshot.child("reservedDate").value.toString().replace("-","/")

                        var finishTextData = dataSnapshot.child("reservedHour").value.toString().toString().split(".")




                        var fHour = ""
                        var fMin = finishTextData[1]
                        if(fMin.length==1) {
                            fMin+="0"
                        }
                        var initialMin = fMin
                        var initialHour = (finishTextData[0].toInt()+1).toString()
                        var minInt = fMin.toInt()+ dataSnapshot.child("duration").value.toString().toInt();
                        if(minInt>= 59){
                            fMin ="00"
                            fHour = (finishTextData[0].toInt()+1).toString()
                        }else{
                            fHour = finishTextData[0]
                            fMin = minInt.toString()
                        }

                        if(initialMin.length==1)initialMin+="0"
                        if(dataSnapshot.child("reservedHour").value.toString().toFloat() < (initialHour+"."+initialMin).toFloat())
                            text_reservation_hour.text = (initialHour.toInt()-1).toString()+":"+initialMin
                        else
                            text_reservation_hour.text = initialHour+":"+initialMin
                        text_reservation_hour_end.text = fHour+":"+fMin
                        var horaReserva = dataSnapshot.child("reservationHour").value.toString().split(":")
                        var horaReservaText = horaReserva[0]+":"+horaReserva[1]

                        if(horaReserva[1].length==1)horaReservaText+="0"

                        Picasso.get().load(dataSnapshot.child("imageUrl").value.toString()).into(image_preview);


                        text_reservation_request.text = "Reserva realitzada el dia "+dataSnapshot.child("reservationDate").value.toString().replace("-","/")+ " a les "+horaReservaText
                    }

                }else{
                    my_reservation_cancel.visibility = View.GONE
                    text_reservation_address.visibility = View.GONE
                    text_reservation_date.visibility = View.GONE
                    reservaPerElDia.visibility = View.GONE
                    text_reservation_hour.visibility = View.GONE
                    text_reservation_hour_end.visibility = View.GONE
                    text_reservation_request.visibility = View.GONE
                    durant.visibility = View.GONE
                    text_reservation_name.text = "No tens cap reserva activa"

                }
            }
        })
    }
}