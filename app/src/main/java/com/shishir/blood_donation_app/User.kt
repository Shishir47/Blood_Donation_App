package com.shishir.blood_donation_app

import com.google.firebase.firestore.PropertyName

data class User(
    @JvmField @PropertyName("Full Name") val fullName: String = "",
    @JvmField @PropertyName("Address") val address: String = "",
    @JvmField @PropertyName("Availablity Status") val availabilityStatus: String = "",
    @JvmField @PropertyName("Blood Group") val bloodGroup: String = "",
    @JvmField @PropertyName("City") val city: String = "",
    @JvmField @PropertyName("Contact Number") val contactNumber: String = "",
    @JvmField @PropertyName("Donor Email") val donorEmail: String = ""
)
