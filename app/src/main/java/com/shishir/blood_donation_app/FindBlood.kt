package com.shishir.blood_donation_app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shishir.blood_donation_app.databinding.ActivityFindBloodBinding

class FindBlood : AppCompatActivity() {

    private lateinit var mBinding: ActivityFindBloodBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: UserAdapter
    private var usersList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_find_blood)
        firestore = FirebaseFirestore.getInstance()

        mBinding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(usersList)
        mBinding.userRecyclerView.adapter = adapter

        val sortByBlood = resources.getStringArray(R.array.sortByBlood)
        val sortByBloodAdapter =
            ArrayAdapter(this, R.layout.spinner_item, sortByBlood)
        mBinding.sortByBlood.adapter = sortByBloodAdapter


        val sortByCity = resources.getStringArray(R.array.sortByCity)
        val sortByCityAdapter =
            ArrayAdapter(this, R.layout.spinner_item, sortByCity)
        mBinding.sortByCity.adapter = sortByCityAdapter


        mBinding.sortByBlood.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedBloodGroup = parent?.getItemAtPosition(position).toString()
                val selectedCity = mBinding.sortByCity.selectedItem.toString()
                filterUsers(selectedBloodGroup, selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        mBinding.sortByCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCity = parent?.getItemAtPosition(position).toString()
                val selectedBloodGroup = mBinding.sortByBlood.selectedItem.toString()
                filterUsers(selectedBloodGroup, selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_find_blood

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_find_blood -> {
                    Toast.makeText(this, "Already on Find Blood", Toast.LENGTH_SHORT).show()
                    true

                }

                R.id.nav_profile -> {
                    Intent(this@FindBlood, Profile::class.java).also {
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


    private fun filterUsers(bloodGroup: String, city: String) {
        var query: Query = firestore.collection("users")
        query = query.whereEqualTo("Availability Status", "Available")
        if (bloodGroup != "All") {
            query = query.whereEqualTo("Blood Group", bloodGroup)
        }
        if (city != "All") {
            query = query.whereEqualTo("City", city)
        }

        query.get()
            .addOnSuccessListener { documents ->
                usersList.clear()
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    usersList.add(user)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
    }
}
