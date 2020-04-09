package com.apptest.nensalparc.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apptest.nensalparc.R
import kotlinx.android.synthetic.main.item_timefraction_background.view.*
import kotlinx.android.synthetic.main.participant_item.view.*

class ParticipantsAdapter: RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>(){
    //                            name    dni
    var usersData:ArrayList<Pair<String, String>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.participant_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        Log.i("Participants", "count"+ usersData.size)
        return usersData.size
    }

    override fun onBindViewHolder(holder: ParticipantsAdapter.ViewHolder, position: Int) {
        holder.dni.text = usersData[position].second
        holder.name.text = usersData[position].first
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val dni = itemView.dni
        val name = itemView.name
        val context = itemView.context
    }

}