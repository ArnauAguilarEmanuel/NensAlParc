package com.apptest.nensalparc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.apptest.nensalparc.adapter.ParticipantsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_display_participats.*
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class DisplayParticipatsActivity : AppCompatActivity() {

    var place:AreaInfoModel = AreaInfoModel()
    var hour:Float = 0f
    var dateId:String = ""
    var fractionDuration:Int = 0
    val db = FirebaseDatabase.getInstance().reference;
    var user: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_participats)
        place = intent.getSerializableExtra("place") as AreaInfoModel
        hour = intent.getFloatExtra("hour", 0f);
        dateId = intent.getStringExtra("date");
        fractionDuration = intent.getIntExtra("fractionDuration",0);

        val userPreferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val userId = userPreferences?.getString("UserId", "")
        val UserName = userPreferences?.getString("UserName", "")
        val UserDNI = userPreferences?.getString("UserDNI", "")
        val UserLocation = userPreferences?.getString("UserLocation", "")
        val context = this;
        user = User(UserName, UserLocation, UserDNI, userId);

        placeNameDisplay.text = place.name;
        timeDisplay.text = dateId.replace("-","/") +"\n"+hour.toString().replace(".",":") +" - "+(hour+(fractionDuration/100f)).toString().replace(".",":")

        lifecycleScope.launch {
            var participantsScrollview = scrollView;

            db.child("Locations").child(user.location.toString()).child("Places").child(place.placeID.toString()).child("SessionData").child("Reservations").child(dateId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var users:ArrayList<String> = ArrayList()
                    dataSnapshot.children.forEach {
                        if(it.value.toString().toFloat()==hour)
                            users.add(it.key.toString())
                    }
                    //                            name    dni
                    var usersData:ArrayList<Pair<String, String>> = ArrayList()
                    users.forEach {
                        db.child("Users").child(it).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(ds: DataSnapshot) {
                                usersData.add(Pair<String,String>(ds.child("name").value.toString(),ds.child("dni").value.toString()))
                                if(usersData.size == users.size){
                                    var adapter = ParticipantsAdapter()
                                    adapter.usersData = usersData
                                    val llm = LinearLayoutManager(context)
                                    llm.orientation = LinearLayoutManager.VERTICAL
                                    participantsScrollview.setLayoutManager(llm)
                                    participantsScrollview.setAdapter(adapter)
                                }
                            }
                        })
                    }
                }

            })
        }



    }

}
