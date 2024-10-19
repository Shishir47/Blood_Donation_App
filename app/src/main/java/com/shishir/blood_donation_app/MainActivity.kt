package com.shishir.blood_donation_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shishir.blood_donation_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mBinding.signInBtn.setOnClickListener{
            login()
        }
        mBinding.signUpBtn.setOnClickListener{
            createAccount()
        }
        mBinding.flipRegisterBtn.setOnClickListener{
            mBinding.flipper.showNext()
        }
        mBinding.flipLoginBtn.setOnClickListener{
            mBinding.flipper.showPrevious()
        }
    }
    private fun saveUserData(email: String, bloodGroup: String, city: String, lastDonated: String) {
        val user = hashMapOf(
            "email" to email,
            "bloodGroup" to bloodGroup,
            "city" to city,
            "lastDonated" to lastDonated
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
        val email = mBinding.registerEmail.editText?.text.toString()
        val pass = mBinding.registerPass.editText?.text.toString()
        val bloodGroup = mBinding.bloodGroup.selectedItem.toString()
        val city = mBinding.city.selectedItem.toString()
        val lastDonated = mBinding.lastDonated.text.toString()
        if(email.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Enter Your Email/Password", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Account was Created", Toast.LENGTH_SHORT).show()
                    saveUserData(email, bloodGroup, city, lastDonated)
                }
                else{
                    Toast.makeText(this, "Account was not Created", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun login() {
        val email= mBinding.loginEmail.editText?.text.toString()
        val pass= mBinding.loginPass.editText?.text.toString().trim()
        if(email.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Enter Your Email/Password", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "User Logged In", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "User Login Failed\nSomething Went Wrong!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}