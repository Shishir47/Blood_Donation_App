package com.shishir.blood_donation_app

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.StyleSpan
import com.shishir.blood_donation_app.databinding.ItemUserBinding

class UserAdapter(private val users: List<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(user: User) {
            val styledText = SpannableStringBuilder()

            styledText.append("Name: ")
            val nameStart = styledText.length - "Name: ".length
            val nameEnd = styledText.length
            styledText.setSpan(
                StyleSpan(Typeface.BOLD),
                nameStart,
                nameEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            styledText.append(user.fullName + "\n")

            styledText.append("Address: ")
            val addressStart = styledText.length - "Address: ".length
            val addressEnd = styledText.length
            styledText.setSpan(
                StyleSpan(Typeface.BOLD),
                addressStart,
                addressEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            styledText.append(user.address + "\n")

            styledText.append("Mobile: ")
            val contactStart = styledText.length - "Mobile: ".length
            val contactEnd = styledText.length
            styledText.setSpan(
                StyleSpan(Typeface.BOLD),
                contactStart,
                contactEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            styledText.append(user.contactNumber + "\n")

            styledText.append("Blood Group: ")
            val bloodGroupStart = styledText.length - "Blood Group: ".length
            val bloodGroupEnd = styledText.length
            styledText.setSpan(
                StyleSpan(Typeface.BOLD),
                bloodGroupStart,
                bloodGroupEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            styledText.append(user.bloodGroup + "\n")

            styledText.append("Availability: ")
            val availabilityStart = styledText.length - "Availability: ".length
            val availabilityEnd = styledText.length
            styledText.setSpan(
                StyleSpan(Typeface.BOLD),
                availabilityStart,
                availabilityEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            styledText.append(user.availabilityStatus + "\n")

            styledText.append("City: ")
            val cityStart = styledText.length - "City: ".length
            val cityEnd = styledText.length
            styledText.setSpan(
                StyleSpan(Typeface.BOLD),
                cityStart,
                cityEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            styledText.append(user.city + "\n")

            styledText.append("Email: ")
            val emailStart = styledText.length - "Email: ".length
            val emailEnd = styledText.length
            styledText.setSpan(
                StyleSpan(Typeface.BOLD),
                emailStart,
                emailEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            styledText.append(user.donorEmail)

            binding.userInfoText.text = styledText

            binding.userInfoText.text = styledText
            Linkify.addLinks(binding.userInfoText, Linkify.PHONE_NUMBERS)
            binding.userInfoText.movementMethod = LinkMovementMethod.getInstance()
        }

    }
}
