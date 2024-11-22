package com.shishir.blood_donation_app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shishir.blood_donation_app.databinding.ActivityChangeDetailsBinding

class ChangeDetails : AppCompatActivity() {
    private lateinit var mBinding: ActivityChangeDetailsBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_details)

        val status = resources.getStringArray(R.array.availableStatus)
        val statusAdapter = ArrayAdapter(this, R.layout.spinner_item, status)
        mBinding.statusCD.adapter = statusAdapter

        val cities = resources.getStringArray(R.array.cities)
        val cityAdapter = ArrayAdapter(this, R.layout.spinner_item, cities)
        mBinding.cityCD.adapter = cityAdapter

        mBinding.changeInfo.setOnClickListener {
            saveInfoChanges()

            val intent = Intent(this@ChangeDetails, Profile::class.java).apply {
                putExtra("Email", FirebaseAuth.getInstance().currentUser?.email)
            }
            startActivity(intent)
            finish()

        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_find_blood -> {
                    Intent(this@ChangeDetails, FindBlood::class.java).also {
                        startActivity(it)
                    }
                    true
                }

                R.id.nav_profile -> {
                    Intent(this@ChangeDetails, Profile::class.java).also {
                        startActivity(it)
                    }
                    true
                }

                R.id.nav_logout -> {
                    showLogoutConfirmationDialog()
                    true
                }

                else -> false
            }
        }
    }
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout Confirmation")
        builder.setMessage("Are you sure you want to log out?")

        builder.setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun saveInfoChanges() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val email = user.email.toString()

        db.collection("users").document(email).get().addOnSuccessListener { document ->
            if (document != null) {
                val fullName = mBinding.registerName.editText?.text.toString()
                    .ifEmpty { document.getString("Full Name") ?: "N/A" }
                val fAddress = mBinding.registerAddress.editText?.text.toString()
                    .ifEmpty { document.getString("Address") ?: "N/A" }
                val mobileNum = mBinding.registerNumber.editText?.text.toString()
                    .ifEmpty { document.getString("Contact Number") ?: "N/A" }

                val bloodGroup = document.getString("Blood Group") ?: "N/A"
                val city = mBinding.cityCD.selectedItem?.toString()
                    ?.ifEmpty{ document.getString("City") ?: "N/A"}
                val status = mBinding.statusCD.selectedItem?.toString()
                    ?: document.getString("Availability Status") ?: "N/A"

                Log.d(
                    "ChangeDetails",
                    "Saving user data: $fullName, $fAddress, $mobileNum, $bloodGroup, $city, $status"
                )
                saveUserData(fullName, fAddress, mobileNum, bloodGroup,
                    city.toString(), email, status)
            } else {
                Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("ChangeDetails", "Failed to retrieve data from Firestore", e)
            Toast.makeText(this, "Failed to retrieve data from Firestore", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun saveUserData(
        fullName: String,
        fAddress: String,
        mobileNum: String,
        bloodGroup: String,
        city: String,
        email: String,
        status: Any
    ) {
        val user = hashMapOf(
            "Full Name" to fullName,
            "Address" to fAddress,
            "Contact Number" to mobileNum,
            "Blood Group" to bloodGroup,
            "City" to city,
            "Donor Email" to email,
            "Availability Status" to status
        )

        db.collection("users").document(email)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("ChangeDetails", "Error saving data", e)
                Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
