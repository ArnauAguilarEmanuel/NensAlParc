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
import com.apptest.nensalparc.HourModel
import com.apptest.nensalparc.R
import com.apptest.nensalparc.TimeFractionModel
import com.apptest.nensalparc.adapter.HourAdapter
import kotlinx.android.synthetic.main.fragment_reservation.*
import kotlinx.android.synthetic.main.fragment_send.recyclerview
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class PlaceActivity : AppCompatActivity() {

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

    fun initUi(){

        var context = this
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
                    }, year, month, day)
                    dpd.show()
                }

                recyclerview.adapter = adapter
                recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

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