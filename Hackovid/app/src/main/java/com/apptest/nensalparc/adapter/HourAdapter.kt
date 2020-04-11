package com.apptest.nensalparc.adapter

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.ColorSpace
import android.net.Uri
import android.opengl.Visibility
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.apptest.nensalparc.*
import com.apptest.nensalparc.ui.send.PlaceActivity
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.collection.LLRBNode
import kotlinx.android.synthetic.main.item_timefraction_background.view.*
import kotlinx.android.synthetic.main.item_timefraction_button.view.*
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import java.math.RoundingMode
import java.text.DecimalFormat
import android.provider.CalendarContract.Events.CONTENT_URI
import kotlin.time.milliseconds

open class HourAdapter(cont : Activity) : RecyclerView.Adapter<HourAdapter.ViewHolder>(){

    var elements = ArrayList<TimeFractionModel>()
    var date: String = ""
    var user: User = User()
    var place: AreaInfoModel = AreaInfoModel()
    val db = FirebaseDatabase.getInstance().reference;
    val baseContext = cont
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_timefraction_button, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return elements.count()
    }

    class DateHolder(var day:Int, var month:Int, var year:Int){}

    fun parseDate(stringDate : String, separator: String): DateHolder {
        var date = DateHolder(0,0,0)

        val parts = stringDate.split(separator)

        if(parts.size>0)
            date.day = parts[0].toInt()
        if(parts.size>1)
            date.month = parts[1].toInt()
        if(parts.size>2)
            date.year = parts[2].toInt()
        return date
    }

    fun datePassed(reservedDate:DateHolder, reservedHour:DateHolder, sessionDuration: Int):Boolean{
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        var nowDate = DateHolder(day,month+1,year)
        var nowHour = DateHolder(hour,minute,0)

        return (getElapsedTime(reservedDate, reservedHour) + sessionDuration) < getElapsedTime(nowDate, nowHour)
    }

    fun getElapsedTime(reservedDate:DateHolder, reservedHour:DateHolder):Double{
        var time = 0.0;
        time += (reservedDate.year -2020) *     525600.0
        time += (reservedDate.month-1) *        43800.0
        time += (reservedDate.day-1) *          1440.0
        time += (reservedHour.day) *            60.0//hour
        time += (reservedHour.month) *          1.0//minutes

        return time;

    }

    fun makeReservation(fraction: TimeFractionModel){
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        db.child("Locations").child(user.location.toString()).child("Places").child(place.placeID.toString()).child("SessionData").child("Reservations").child(date).child(user.uId.toString()).setValue(fraction.start)
        var reservation = Reservation();

        reservation.address = place.address;
        reservation.imageUrl = place.imageUrl;
        reservation.name = place.name
        reservation.placeID = place.placeID

        reservation.reservationDate =day.toString() + "-" + (month + 1) + "-" + year
        if(minute<10)
            reservation.reservationHour =hour.toString() + ":" + "0"+minute
        else
            reservation.reservationHour =hour.toString() + ":" + minute
        reservation.reservedDate = date
        reservation.reservedHour = fraction.start.toString()
        reservation.duration = fraction.duration.toString();
        db.child("Users").child(user.uId.toString()).child("Reservation").setValue(reservation)

        val builder = AlertDialog.Builder(fullcontext)
        builder.setTitle("Vols crear un recordatori?")
        builder.setMessage("El recordatori serà 30 minuts avants de la reserva")
        builder.setPositiveButton("Confirmar",{ i, Int ->

            val permissions = arrayOf(android.Manifest.permission.WRITE_CALENDAR)
            ActivityCompat.requestPermissions(baseContext ,permissions, 0)

            if (ActivityCompat.checkSelfPermission(fullcontext!!, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                val calID: Long = 3 // Make sure to which calender you want to add event

                var startMillis: Long = 0
                var endMillis: Long = 0

                val startTime = getElapsedTime(DateHolder(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH), DateHolder(Calendar.HOUR_OF_DAY, Calendar.MINUTE, 0))

                var endDateTime = date.split("-")
                var endDateHour = reservation.reservedHour!!.split(".")
                var reminderTime = endDateHour[0].toInt() * 60 + endDateHour[1].toInt() - 30;

                val reservedTime = getElapsedTime(DateHolder(endDateTime[0].toInt(), endDateTime[1].toInt(), endDateTime[2].toInt()), DateHolder(reminderTime/60, reminderTime%60, 0))
                var daysLeft = reservedTime - startTime;


                val beginTime = Calendar.getInstance().timeInMillis + daysLeft*60000
                val endTime = beginTime + fraction.duration!! * 60000


                val cr = baseContext.contentResolver
                val values = ContentValues()
                values.put(CalendarContract.Events.DTSTART, beginTime)
                values.put(CalendarContract.Events.DTEND, endTime)
                values.put(CalendarContract.Events.TITLE, "Reserva a " + reservation.name)
                values.put(CalendarContract.Events.CALENDAR_ID, calID)
                values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

                val uri =  cr.insert(CONTENT_URI, values)
                var eventID = uri?.lastPathSegment!!.toLong()

                var debug = Calendar.getInstance().timeInMillis.toString()
                Toast.makeText(fullcontext,"Reservat amb recordatori!" + '\n' + debug, Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(fullcontext,"Reservat sense recordatori!", Toast.LENGTH_LONG).show();
            }


        })
        builder.setNegativeButton("Cancelar",{ i, Int ->
            Toast.makeText(fullcontext,"Reservat sense recordatori!", Toast.LENGTH_LONG).show();
        })

        builder.show()

    }

    fun prepareUI(holder: ViewHolder, position: Int){
        val element = elements[position]
        holder.reservTextExtra.visibility =View.VISIBLE
        holder.reserv1.visibility = View.VISIBLE
        holder.reserv2.visibility = View.VISIBLE
        holder.reserv3.visibility = View.VISIBLE
        holder.reserv4.visibility = View.VISIBLE
        holder.reserv5.visibility = View.VISIBLE
        holder.full.visibility = View.VISIBLE

        var mode = fullcontext?.resources?.configuration?.uiMode
        var nightModeFlags = Configuration.UI_MODE_NIGHT_MASK
        if(mode != null)
            nightModeFlags = mode and Configuration.UI_MODE_NIGHT_MASK;

        if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES){
            holder.full.setBackgroundColor(Color.parseColor("#BC505050"))
        }else{
            holder.full.setBackgroundColor(Color.parseColor("#75505050"))
        }

        //holder.hour.text = element.start.toString() + "h"
        var i = 1020
        if(element.currentCapacity!! > 5)
            holder.reservTextExtra.text = "+"+(element.currentCapacity - 5).toString()
        else{
            holder.reservTextExtra.visibility = View.GONE
            if(element.currentCapacity!! < 5)
                holder.reserv5.visibility = View.GONE
            if(element.currentCapacity!! < 4)
                holder.reserv4.visibility = View.GONE
            if(element.currentCapacity!! < 3)
                holder.reserv3.visibility = View.GONE
            if(element.currentCapacity!! < 2)
                holder.reserv2.visibility = View.GONE
            if(element.currentCapacity!! < 1)
                holder.reserv1.visibility = View.GONE
        }
        var hour = element.start.toString().split(".")
        if(datePassed(parseDate(date,"-"), DateHolder(hour[0].toInt(), hour[1].toInt(),0),0)||element.currentCapacity>= element.maxCapacity!!)
            holder.full.visibility = View.VISIBLE
        else
            holder.full.visibility = View.GONE

        var textData = element.start.toString().split(".")
        var min = textData[1]
        if(min.length==1) min+="0"
        holder.endTime.text = textData[0]+":"+min
    }
    var fullcontext: Context? = null
    open fun forOnBindViewHolder(holder: ViewHolder, position: Int){
        val element = elements[position]

        prepareUI(holder, position)

        var textData = element.start.toString().split(".")
        var min = textData[1]

        holder.button.setOnClickListener(){
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            db.child("Users").child(user.uId.toString()).child("Reservation").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var hour = element.start.toString().split(".")
                    if(!datePassed(parseDate(date,"-"), DateHolder(hour[0].toInt(), hour[1].toInt(),0),0)){
                        if(dataSnapshot.exists()){
                            var reservedDate = parseDate(dataSnapshot.child("reservedDate").value.toString(),"-")
                            var reservedHour = parseDate(dataSnapshot.child( "reservedHour").value.toString(),".")
                            if(datePassed(reservedDate, reservedHour, dataSnapshot.child("duration").value.toString().toInt())){
                                if(element.currentCapacity != element.maxCapacity){
                                    val builder = AlertDialog.Builder(fullcontext)
                                    builder.setTitle("Confirmar reserva?")

                                    holder.button.text = element.currentCapacity.toString() + "/" + element.maxCapacity.toString()
                                    var finishTextData = element.start.toString().split(".")
                                    var fHour = ""
                                    var fMin = finishTextData[1]
                                    if(fMin.length==1) {
                                        fMin+="0"
                                    }
                                    var minInt = fMin.toInt()+ element.duration!!;
                                    if(minInt>= 60){
                                        fMin ="00"
                                        fHour = (finishTextData[0].toInt()+1).toString()
                                    }else{
                                        fHour = finishTextData[0]
                                    }
                                    var minn = min.toString()
                                    if(min.length==1)minn+="0"
                                    builder.setMessage("Confirma la reserva el dia"+date+" a les "+textData[0]+":"+minn+" i fins a les "+fHour+":"+fMin+"?")
                                    builder.setPositiveButton("Confirmar",{ i, Int ->
                                        makeReservation(element);
                                    })
                                    builder.setNegativeButton("Cancelar",{ i, Int ->})


                                    builder.show()


                                }
                            }else{
                                val builder = AlertDialog.Builder(fullcontext)
                                builder.setTitle("Ja tens una reserva")
                                builder.setMessage("Vols canviar-la per aquesta?")
                                builder.setPositiveButton("Si",{ i, Int ->
                                    db.child("Locations").child(user.location.toString()).child("Places").child(dataSnapshot.child("placeID").value.toString()).child("SessionData")
                                        .child("Reservations").child(dataSnapshot.child("reservedDate").value.toString()).child(user.uId.toString()).removeValue()
                                    makeReservation(element);
                                })
                                builder.setNegativeButton("No ",{ i, Int ->})
                                builder.show()

                            }
                        }else{
                            val builder = AlertDialog.Builder(fullcontext)
                            builder.setTitle("Confirmar reserva?")

                            holder.button.text = element.currentCapacity.toString() + "/" + element.maxCapacity.toString()
                            var finishTextData = element.start.toString().split(".")
                            var fHour = ""
                            var fMin = finishTextData[1]
                            if(fMin.length==1) {
                                fMin+="0"
                            }
                            var minInt = fMin.toInt()+ element.duration!!;
                            if(minInt>= 60){
                                fMin ="00"
                                fHour = (finishTextData[0].toInt()+1).toString()
                            }else{
                                fHour = finishTextData[0]
                                fMin = minInt.toString()
                            }
                            var minn = min.toString()
                            if(min.length==1)minn+="0"
                            builder.setMessage("Confirma la reserva el dia "+date.replace("-","/")+" a les "+textData[0]+":"+minn+" i fins a les "+fHour+":"+fMin+"?")
                            builder.setPositiveButton("Confirmar",{ i, Int ->
                                makeReservation(element);
                            })
                            builder.setNegativeButton("Cancelar",{ i, Int ->})
                            builder.show()
                        }
                    }
                }

            })

        }


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fullcontext = holder.context
        forOnBindViewHolder(holder, position);
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val endTime = itemView.endTime
        val full = itemView.full
        val reserv1 = itemView.reserv1
        val reserv2 = itemView.reserv2
        val reserv3 = itemView.reserv3
        val reserv4 = itemView.reserv4
        val reserv5 = itemView.reserv5
        val reservTextExtra = itemView.reservTextExtra
        val button = itemView.button_timefraction
        val context = itemView.context
    }
}