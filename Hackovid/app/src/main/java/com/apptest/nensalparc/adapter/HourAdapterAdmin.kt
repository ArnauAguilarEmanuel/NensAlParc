package com.apptest.nensalparc.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.apptest.nensalparc.*
import com.apptest.nensalparc.ui.SignInActivity
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

class HourAdapterAdmin : HourAdapter(){


    override fun forOnBindViewHolder(holder: HourAdapter.ViewHolder, position: Int){
        val element = elements[position]

        //holder.hour.text = element.start.toString() + "h"
//
        //Log.i("FRACTION", element.timeFractions?.count().toString())
//
        //holder.background.removeAllViews()
//
        //var i = 1020
        //element.timeFractions?.forEach {
        //    var fraction = it;
        //    var inflatedLayout = LayoutInflater.from(holder.context).inflate(R.layout.item_timefraction_button, holder.background, true)
        //    inflatedLayout.button_timefraction.text = it.currentCapacity.toString() + "/" + it.maxCapacity.toString()
//
        //    inflatedLayout.button_timefraction.setOnClickListener(){
        //        val intent = Intent(holder.context, DisplayParticipatsActivity::class.java).apply {
        //            putExtra("place", place)
        //            putExtra("hour",fraction.start)
        //            putExtra("date",date)
        //            putExtra("fractionDuration",fraction.duration)
        //        }
        //        holder.context.startActivity(intent)
        //    }
        //    inflatedLayout.button_timefraction.id = i
        //    i++
//
        //}
    }

}