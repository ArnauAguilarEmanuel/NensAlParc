package com.apptest.nensalparc.ui.slideshow

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.apptest.nensalparc.R
import com.apptest.nensalparc.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_slideshow.*
import kotlinx.coroutines.launch

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProviders.of(this).get(SlideshowViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        return root
    }

    val db = FirebaseDatabase.getInstance().reference;
    var user = User()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = activity?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val userId = userPreferences?.getString("UserId", "")
        val UserName = userPreferences?.getString("UserName", "")
        val UserDNI = userPreferences?.getString("UserDNI", "")
        val UserLocation = userPreferences?.getString("UserLocation", "")

        user = User(UserName, UserLocation, UserDNI, userId);

        lifecycleScope.launch {
            db.child("Users").child(user.uId.toString()).child("admin").addListenerForSingleValueEvent(  object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    switchButton.isChecked =
                        dataSnapshot.exists();
                }
            })
        }





        switchButton.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener() { buttonView,  isChecked ->

            if(isChecked)
                db.child("Users").child(user.uId.toString()).child("admin").setValue(true);
            else
                db.child("Users").child(user.uId.toString()).child("admin").removeValue();

        })
    }
}
