package com.shishir.blood_donation_app

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shishir.blood_donation_app.SveData.Constants
import com.shishir.blood_donation_app.databinding.ActivityProfileBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class Profile : AppCompatActivity() {
    companion object {
        private const val REQUEST_WRITE_STORAGE = 100
    }

    private lateinit var mBinding: ActivityProfileBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userEmail: String
    private lateinit var fNameDisp: String
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        userEmail = intent.getStringExtra(Constants.EMAIL) ?: return
        userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        fetchUserData()

        mBinding.editIcon.setOnClickListener {
            Intent(this@Profile, ChangeDetails::class.java).also {
                startActivity(it)
                onPause()
            }
        }
        mBinding.userStatus.setOnClickListener {
            Intent(this@Profile, ChangeDetails::class.java).also {
                startActivity(it)
                onPause()
            }
        }

        checkForUpdates()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
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
                    showLogoutConfirmationDialog()
                    true
                }

                else -> false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkForUpdates()
            } else {
                Toast.makeText(this, "Permission required to download updates", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun checkForUpdates() {
        val url =
            "https://raw.githubusercontent.com/Shishir47/Blood_Donation_App/master/version.json"

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@Profile, "Failed to check for updates", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val json = JSONObject(responseBody.string())
                        val latestVersionCode = json.getInt("versionCode")
                        val apkUrl = json.getString("apkUrl")
                        val currentVersionCode =
                            packageManager.getPackageInfo(packageName, 0).versionCode

                        if (latestVersionCode > currentVersionCode) {
                            runOnUiThread {
                                showUpdateDialog(apkUrl)
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@Profile,
                            "Failed to fetch update info",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun showUpdateDialog(apkUrl: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New Update Available")
        builder.setMessage("A newer version of the app is available. Please update to the latest version.")
        builder.setPositiveButton("Update") { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl))
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.setCancelable(false)
        builder.show()
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

    private fun fetchUserData() {
        if (!::userEmail.isInitialized) return

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

    override fun onResume() {
        super.onResume()
        fetchUserData()
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
