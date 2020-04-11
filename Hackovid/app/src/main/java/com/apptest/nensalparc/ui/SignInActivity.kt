package com.apptest.nensalparc.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.apptest.nensalparc.MainActivity
import com.apptest.nensalparc.R
import com.apptest.nensalparc.Resources.CountryCodes
import com.apptest.nensalparc.VerifyPhoneActivity
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        button.setOnClickListener(View.OnClickListener {
            var number = editText.text.toString();
            if(number.isEmpty()){
                editText.setError("Cal un numero de telefon");
                editText.requestFocus()
            }else{
                val intent = Intent(this, VerifyPhoneActivity::class.java).apply {
                    putExtra("phoneNumber", "+34"+number)
                }
                startActivity(intent);
            }
        })
    }
}
