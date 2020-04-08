package com.apptest.nensalparc

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.arch.core.executor.TaskExecutor
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_verify_phone.*
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class VerifyPhoneActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)

        var phoneNumer = intent.getStringExtra("phoneNumber");
        Log.i("Id", phoneNumer)

        sendVerificationCode(phoneNumer)

        button.setOnClickListener(View.OnClickListener {
            var code = editText.text.toString();
            if(code.isEmpty()){
                editText.setError("Enter valid code");
                editText.requestFocus();

            }else{
                verifyCode(code);
            }
        })

    }

    private fun verifyCode(code:String){
        Log.i("Id", storedVerificationId)
        var credential = PhoneAuthProvider.getCredential(storedVerificationId, code)

        signInWithCredential(credential);

    }


    private fun signInWithCredential(credential: PhoneAuthCredential) {
        var act = this;
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener({
            if(it.isSuccessful){

                val user = it.result?.user



                // if user does not exist create
                var db = FirebaseDatabase.getInstance().reference.root;
                db.child("Users").child(user?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if(dataSnapshot.exists()){
                            val intent = Intent(baseContext, MainActivity::class.java).apply {
                            }
                            val editor = baseContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE).edit()
                            editor.putString("UserId", user?.uid.toString())
                            editor.putString("UserName", dataSnapshot.child("name").value.toString())
                            editor.putString("UserDNI", dataSnapshot.child("dni").value.toString())
                            editor.putString("UserLocation", dataSnapshot.child("location").value.toString())
                            editor.apply()

                            val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                            ActivityCompat.requestPermissions(act, permissions,0)

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent);
                        }else{
                            val intent = Intent(baseContext, RegisterActivity::class.java).apply {
                                this.putExtra("UserId", user?.uid.toString())
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent);
                        }

                    }

                })


            }else{
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG)
            }
        })
    }

    private fun sendVerificationCode(number: String){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number,
            60,
            TimeUnit.SECONDS,
            this,
            mCallBack
        )
    }

    var storedVerificationId: String = ""
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken;
    var errorText:String = "";

    var mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {



        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            errorDisplay.text = "onVerificationCompleted:$credential";
            Log.d("Id", "onVerificationCompleted:$credential")
            if(credential.smsCode == null){
                signInWithCredential(credential)
            }else
                verifyCode(credential.smsCode!!)

        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("Id", "onVerificationFailed", e)
            errorText = e.toString();
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                errorText = "FirebaseAuthInvalidCredentialsException"
                // ...
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                errorText = "FirebaseTooManyRequestsException"

                // ...
            }
            errorDisplay.text = errorText;
            // Show a message and update the UI
            // ...
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            errorDisplay.text = "onCodeSent:$verificationId"
            Log.d("Id", "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later

            storedVerificationId = verificationId
            resendToken = token

            // ...
        }
    }
}
