package com.shishir.blood_donation_app

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.shishir.blood_donation_app.databinding.ActivityChangeDetailsBinding
import com.shishir.blood_donation_app.databinding.ActivityMainBinding

class ChangeDetails : AppCompatActivity() {
    private lateinit var mBinding: ActivityChangeDetailsBinding
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_details)
        val bloodGroups = resources.getStringArray(R.array.blood_groups)
        val bloodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups)
        mBinding.bloodGroup.adapter = bloodAdapter

        val cities = resources.getStringArray(R.array.cities)
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cities)
        mBinding.city.adapter = cityAdapter


        mBinding.changeInfo.setOnClickListener{
            saveInfoChanges()
        }
    }

    private fun saveInfoChanges() {

    }

}