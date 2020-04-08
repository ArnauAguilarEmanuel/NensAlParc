package com.apptest.nensalparc.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.apptest.nensalparc.AreaInfoModel
import com.apptest.nensalparc.HourModel
import com.apptest.nensalparc.R
import com.apptest.nensalparc.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.collection.LLRBNode
import kotlinx.android.synthetic.main.item_timefraction_background.view.*
import kotlinx.android.synthetic.main.item_timefraction_button.view.*
import java.sql.Timestamp

class HourAdapter : RecyclerView.Adapter<HourAdapter.ViewHolder>(){

    var elements = ArrayList<HourModel>()
    var date: String = ""
    var user: User = User()
    var place: AreaInfoModel = AreaInfoModel()
    val db = FirebaseDatabase.getInstance().reference;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_timefraction_background, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return elements.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val element = elements[position]

        holder.hour.text = element.start.toString() + "h"

        Log.i("FRACTION", element.timeFractions?.count().toString())

        holder.background.removeAllViews()

        var i = 1020
        element.timeFractions?.forEach {
            var fraction = it;
            var inflatedLayout = LayoutInflater.from(holder.background.context).inflate(R.layout.item_timefraction_button, holder.background, true)
            inflatedLayout.button_timefraction.text = it.currentCapacity.toString() + "/" + it.maxCapacity.toString()
            inflatedLayout.button_timefraction.setOnClickListener(){
                if(fraction.currentCapacity != fraction.maxCapacity)
                    db.child("Locations").child(user.location.toString()).child("Places").child(place.placeID.toString()).child("SessionData").child("Reservations").child(date).child(user.uId.toString()).setValue(fraction.start)
            }
            inflatedLayout.button_timefraction.id = i
            i++

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val hour = itemView.text_hour
        val background = itemView.image_background
    }
}