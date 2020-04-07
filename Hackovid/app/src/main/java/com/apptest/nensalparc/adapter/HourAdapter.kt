package com.apptest.nensalparc.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.apptest.nensalparc.HourModel
import com.apptest.nensalparc.R
import kotlinx.android.synthetic.main.item_timefraction_background.view.*
import java.sql.Timestamp

class HourAdapter : RecyclerView.Adapter<HourAdapter.ViewHolder>(){

    var elements = ArrayList<HourModel>()

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

        element.timeFractions?.forEach {
            val timeFraction = LayoutInflater.from(holder.background.context).inflate(R.layout.item_timefraction_button, holder.background, false)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val hour = itemView.text_hour
        val background = itemView.image_background
    }
}