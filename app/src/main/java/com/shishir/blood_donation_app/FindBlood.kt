package com.shishir.blood_donation_app

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
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

        // Setup RecyclerView
        mBinding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(usersList)
        mBinding.userRecyclerView.adapter = adapter

        // Setup Blood Group Spinner
        val sortByBlood = resources.getStringArray(R.array.sortByBlood)
        val sortByBloodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortByBlood)
        mBinding.sortByBlood.adapter = sortByBloodAdapter

        // Setup City Spinner
        val sortByCity = resources.getStringArray(R.array.sortByCity)
        val sortByCityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortByCity)
        mBinding.sortByCity.adapter = sortByCityAdapter

        // Set listeners for both spinners
        mBinding.sortByBlood.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedBloodGroup = parent?.getItemAtPosition(position).toString()
                val selectedCity = mBinding.sortByCity.selectedItem.toString()
                filterUsers(selectedBloodGroup, selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        mBinding.sortByCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCity = parent?.getItemAtPosition(position).toString()
                val selectedBloodGroup = mBinding.sortByBlood.selectedItem.toString()
                filterUsers(selectedBloodGroup, selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun filterUsers(bloodGroup: String, city: String) {
        var query: Query = firestore.collection("users")
        query= query.whereEqualTo("Availablity Status", "Available")
        if (bloodGroup != "All") {
            query = query.whereEqualTo("Blood Group", bloodGroup)
        }
        if (city != "All") {
            query = query.whereEqualTo("City", city)
        }

        // Get filtered users
        query.get()
            .addOnSuccessListener { documents ->
                usersList.clear()  // Clear the list before adding new results
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    usersList.add(user)  // Add each valid user to the list
                }
                adapter.notifyDataSetChanged()  // Update the RecyclerView
            }
            .addOnFailureListener { e ->
                // Handle any errors (e.g., show a toast message)
            }
    }
}