package com.apptest.nensalparc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import com.apptest.nensalparc.ui.SignInActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions,0)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_map,
                R.id.nav_reservation
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

// Write a message to the database



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)



        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)


        val userPreferences = baseContext?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val userId = userPreferences?.getString("UserId", "")

        if(userId != "") {
            val navView: NavigationView = findViewById(R.id.nav_view)
            navView.dni.text = userPreferences?.getString("UserDNI", "")
            navView.userName.text = userPreferences?.getString("UserName", "")
        }

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    }

    override fun onResume() {
        super.onResume()

        val userPreferences = baseContext?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val userId = userPreferences?.getString("UserId", "")

        if(userId == ""){

            //val intent = Intent(this, SignInActivity::class.java).apply {
            //}
            //startActivity(intent)

            return;
        }
    }
}
