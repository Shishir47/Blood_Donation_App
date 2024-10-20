package com.shishir.blood_donation_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shishir.blood_donation_app.SveData.Constants
import com.shishir.blood_donation_app.databinding.ActivityProfileBinding

class Profile : AppCompatActivity() {
    private lateinit var mBinding: ActivityProfileBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userEmail: String
    private lateinit var fNameDisp: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        userEmail = intent.getStringExtra(Constants.EMAIL) ?: return
        fetchUserData()

        mBinding.editIcon.setOnClickListener {
            Intent(this@Profile, ChangeDetails::class.java).also {
                startActivity(it)
            }
        }
        mBinding.userStatus.setOnClickListener {
            Intent(this@Profile, ChangeDetails::class.java).also {
                startActivity(it)
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_find_blood -> {
                    Intent(this@Profile, FindBlood::class.java).also {
                        startActivity(it)
                    }
                    true
                }

                R.id.nav_profile -> {
                    Toast.makeText(this, "Already on Profile", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }

    private fun fetchUserData() {
        db.collection("users").document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fullName = document.getString("Full Name") ?: "N/A"
                    val address = document.getString("Address") ?: "N/A"
                    val mobileNumber = document.getString("Contact Number") ?: "N/A"
                    val bloodGroup = document.getString("Blood Group") ?: "N/A"
                    val city = document.getString("City") ?: "N/A"
                    val availabilityStatus = document.getString("Availability Status") ?: "N/A"
                    fNameDisp = fullName
                    setGreetingMessage()
                    mBinding.userInfoText.text =
                        "Name: $fullName\n\nAddress: $address\n\nMobile Number: $mobileNumber\n\nBlood Group: $bloodGroup\n\nCity: $city"
                    mBinding.userStatus.text = "Status: $availabilityStatus"
                } else {
                    Toast.makeText(this, "No such user found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error fetching user data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setGreetingMessage() {
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when {
            currentHour < 12 -> "Good Morning"
            currentHour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        fNameDisp = fNameDisp.substringBefore(' ')
        mBinding.greetingTextView.text = "$greeting, \n$fNameDisp"
    }
}
