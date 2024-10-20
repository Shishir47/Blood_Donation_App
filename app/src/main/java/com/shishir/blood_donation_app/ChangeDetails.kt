package com.shishir.blood_donation_app

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shishir.blood_donation_app.databinding.ActivityChangeDetailsBinding

class ChangeDetails : AppCompatActivity() {
    private lateinit var mBinding: ActivityChangeDetailsBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_details)

        // Set up the spinners with the available values
        val status = resources.getStringArray(R.array.availableStatus)
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, status)
        mBinding.status.adapter = statusAdapter

        val cities = resources.getStringArray(R.array.cities)
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cities)
        mBinding.city.adapter = cityAdapter

        // Set up button listeners
        mBinding.changeInfo.setOnClickListener {
            saveInfoChanges()
            Intent(this@ChangeDetails, Profile::class.java).also{
                startActivity(it)
                finish()
            }
        }

        mBinding.cancelInfo.setOnClickListener {
            Intent(this@ChangeDetails, Profile::class.java).also{
                startActivity(it)
                finish()
            }
        }
    }

    private fun saveInfoChanges() {
        // Check if the user is logged in and retrieve their email
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val email = user.email.toString()

        // Fetch the current data from Firestore
        db.collection("users").document(email).get().addOnSuccessListener { document ->
            if (document != null) {
                // TextField input handling
                val fullName = mBinding.registerName.editText?.text.toString().ifEmpty { document.getString("Full Name") ?: "N/A" }
                val fAddress = mBinding.registerAddress.editText?.text.toString().ifEmpty { document.getString("Address") ?: "N/A" }
                val mobileNum = mBinding.registerNumber.editText?.text.toString().ifEmpty { document.getString("Contact Number") ?: "N/A" }

                // Spinner input handling
                val bloodGroup = document.getString("Blood Group") ?: "N/A"  // Blood group remains unchanged
                val city = mBinding.city.selectedItem?.toString() ?: document.getString("City") ?: "N/A"
                val status = mBinding.status.selectedItem?.toString() ?: document.getString("Availability Status") ?: "N/A"

                // Save updated user data
                saveUserData(fullName, fAddress, mobileNum, bloodGroup, city, email, status)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to retrieve data from Firestore", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserData(fullName: String, fAddress: String, mobileNum: String, bloodGroup: String, city: String, email: String, status: Any) {
        val user = hashMapOf(
            "Full Name" to fullName,
            "Address" to fAddress,
            "Contact Number" to mobileNum,
            "Blood Group" to bloodGroup,
            "City" to city,
            "Email" to email,
            "Availability Status" to status
        )

        // Save the data to Firestore
        db.collection("users").document(email)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
