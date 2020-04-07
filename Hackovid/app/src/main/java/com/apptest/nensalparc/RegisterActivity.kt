package com.apptest.nensalparc

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        var userId = intent.getStringExtra("UserId")

        registerButton.setOnClickListener({
            var db = FirebaseDatabase.getInstance().reference;


            Log.i("Id", userId);
            val editor = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE).edit()
            editor.putString("UserId", userId)
            var user = User(inputName.text.toString(), inputLocation.text.toString(), inputDNI.text.toString(), userId)
            editor.putString("UserName", user.name)
            editor.putString("UserDNI", user.DNI)
            editor.putString("UserLocation", user.location)
            editor.apply()


            db.child("Users").child(userId).setValue(user);



            val intent = Intent(this, MainActivity::class.java).apply {
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent);
        })
    }


}
