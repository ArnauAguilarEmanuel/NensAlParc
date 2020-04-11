package com.apptest.nensalparc.adapter

import android.app.Activity
import android.app.AlertDialog
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

class HourAdapterAdmin (act : Activity) : HourAdapter(act){


    override fun forOnBindViewHolder(holder: HourAdapter.ViewHolder, position: Int){
        val element = elements[position]
        prepareUI(holder, position)

        holder.button.setOnClickListener(){
            val intent = Intent(holder.context, DisplayParticipatsActivity::class.java).apply {
                putExtra("place", place)
                putExtra("hour",element.start)
                putExtra("date",date)
                putExtra("fractionDuration",element.duration)
            }
            holder.context.startActivity(intent)
        }



    }

}