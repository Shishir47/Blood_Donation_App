package com.shishir.blood_donation_app

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shishir.blood_donation_app.databinding.ActivityMainBinding
import com.shishir.blood_donation_app.SveData.Constants


class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra(Constants.EMAIL, FirebaseAuth.getInstance().currentUser?.email)
            startActivity(intent)
            finish()
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val bloodGroups = resources.getStringArray(R.array.blood_groups)
        val bloodAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups)
        mBinding.bloodGroup.adapter = bloodAdapter

        val cities = resources.getStringArray(R.array.cities)
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cities)
        mBinding.city.adapter = cityAdapter

        val availability = resources.getStringArray(R.array.availableStatus)
        val availabilityAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, availability)
        mBinding.availability.adapter = availabilityAdapter

        mBinding.bloodGroup.isEnabled = true
        mBinding.bloodGroup.isClickable = true
        mBinding.bloodGroup.isFocusable = true

        mBinding.city.isEnabled = true
        mBinding.city.isClickable = true
        mBinding.city.isFocusable = true

        mBinding.availability.isEnabled = true
        mBinding.availability.isClickable = true
        mBinding.availability.isFocusable = true

        mBinding.signInBtn.setOnClickListener {
            login()
        }
        mBinding.signUpBtn.setOnClickListener {
            createAccount()
        }
        mBinding.flipRegisterBtn.setOnClickListener {
            mBinding.flipper.showNext()
        }
        mBinding.flipLoginBtn.setOnClickListener {
            mBinding.flipper.showPrevious()
        }

        val bloodDataSpinner: Spinner = findViewById(R.id.blood_group)
        val bloodDataAdapter = ArrayAdapter.createFromResource(this,
            R.array.blood_groups, R.layout.spinner_item)
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodDataSpinner.adapter = bloodDataAdapter

        val cityDataSpinner: Spinner = findViewById(R.id.city)
        val cityDataAdapter = ArrayAdapter.createFromResource(this,
            R.array.cities, R.layout.spinner_item)
        cityDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cityDataSpinner.adapter = cityDataAdapter

        val ableDataSpinner: Spinner = findViewById(R.id.availability)
        val ableDataAdapter = ArrayAdapter.createFromResource(this,
            R.array.availableStatus, R.layout.spinner_item)
        ableDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ableDataSpinner.adapter = ableDataAdapter

    }

    private fun saveUserData(
        fullName: String,
        fAddress: String,
        mobileNum: String,
        email: String,
        bloodGroup: String,
        city: String,
        status: String
    ) {
        val user = hashMapOf(
            "Full Name" to fullName,
            "Address" to fAddress,
            "Contact Number" to mobileNum,
            "Donor Email" to email,
            "Blood Group" to bloodGroup,
            "City" to city,
            "Availability Status" to status
        )

        db.collection("users").document(email)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createAccount() {
        val fullName = mBinding.registerName.editText?.text.toString()
        val fAddress = mBinding.registerAddress.editText?.text.toString()
        val mobileNum = mBinding.registerNumber.editText?.text.toString()
        val email = mBinding.registerEmail.editText?.text.toString()
        val pass = mBinding.registerPass.editText?.text.toString()
        val bloodGroup = mBinding.bloodGroup.selectedItem.toString()
        val city = mBinding.city.selectedItem.toString()
        val status = mBinding.availability.selectedItem.toString()
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Enter Your Email/Password", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUserData(fullName, fAddress, mobileNum, email, bloodGroup, city, status)
                    Toast.makeText(this, "Account was Created", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Account was not Created", Toast.LENGTH_SHORT).show()
                }
            }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User Logged In", Toast.LENGTH_SHORT).show()
                    if (email.isNotEmpty()) {
                        Intent(this@MainActivity, Profile::class.java).also {
                            it.putExtra(Constants.EMAIL, email)
                            startActivity(it)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "User Login Failed\nSomething Went Wrong!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun login() {
        val email = mBinding.loginEmail.editText?.text.toString()
        val pass = mBinding.loginPass.editText?.text.toString().trim()
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Enter Your Email/Password", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User Logged In", Toast.LENGTH_SHORT).show()
                    if (email.isNotEmpty()) {
                        Intent(this@MainActivity, Profile::class.java).also {
                            it.putExtra(Constants.EMAIL, email)
                            startActivity(it)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "User Login Failed\nSomething Went Wrong!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}