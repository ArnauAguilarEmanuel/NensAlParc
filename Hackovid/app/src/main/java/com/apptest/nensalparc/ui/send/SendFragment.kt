package com.apptest.nensalparc.ui.send

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apptest.nensalparc.HourModel
import com.apptest.nensalparc.R
import com.apptest.nensalparc.TimeFractionModel
import com.apptest.nensalparc.adapter.HourAdapter
import kotlinx.android.synthetic.main.fragment_reservation.*
import kotlinx.android.synthetic.main.fragment_reservation.view.*
import kotlinx.android.synthetic.main.fragment_send.*
import kotlinx.android.synthetic.main.fragment_send.recyclerview
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SendFragment : Fragment() {

    private lateinit var sendViewModel: SendViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{

        return inflater.inflate(R.layout.fragment_reservation, container, false)
    }

    private val adapter = HourAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view,savedInstanceState)

        lifecycleScope.launch{
            try {

                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                button_select_day.text = day.toString() + "/" + (month + 1) + "/" + year

                button_select_day.setOnClickListener {
                    val dpd = DatePickerDialog(this@SendFragment.requireContext(), DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->
                        button_select_day.text = mDay.toString() + "/" + (mMonth + 1) + "/" + mYear
                    }, year, month, day)
                    dpd.show()
                }

                recyclerview.adapter = adapter
                recyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                val hours = ArrayList<HourModel>()
                hours.add(HourModel(9, ArrayList<TimeFractionModel>()))
                hours[0].timeFractions?.add(TimeFractionModel(0, 15, 10, 2))
                hours[0].timeFractions?.add(TimeFractionModel(15, 15, 8, 5))
                hours[0].timeFractions?.add(TimeFractionModel(30, 15, 10, 2))
                hours[0].timeFractions?.add(TimeFractionModel(45, 15, 6, 4))

                hours.add(HourModel(10, ArrayList<TimeFractionModel>()))
                hours[1].timeFractions?.add(TimeFractionModel(0, 15, 10, 2))
                hours[1].timeFractions?.add(TimeFractionModel(15, 15, 10, 2))
                hours[1].timeFractions?.add(TimeFractionModel(30, 15, 10, 2))
                hours[1].timeFractions?.add(TimeFractionModel(45, 15, 10, 2))

                hours.add(HourModel(11, ArrayList<TimeFractionModel>()))
                hours.add(HourModel(12, ArrayList<TimeFractionModel>()))
                hours.add(HourModel(13, ArrayList<TimeFractionModel>()))
                hours.add(HourModel(14, ArrayList<TimeFractionModel>()))
                hours.add(HourModel(15, ArrayList<TimeFractionModel>()))
                hours.add(HourModel(16, ArrayList<TimeFractionModel>()))
                hours.add(HourModel(17, ArrayList<TimeFractionModel>()))
                hours.add(HourModel(18, ArrayList<TimeFractionModel>()))
                hours.add(HourModel(19, ArrayList<TimeFractionModel>()))

                adapter.elements = hours
                adapter.notifyDataSetChanged()

            } catch (e: IOException){
                //No Internet or Server down
                Log.w("StreamsFragment", "Request couldn't be executed")
            }
        }
    }
}