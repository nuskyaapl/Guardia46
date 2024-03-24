package com.se46.guardia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ManageContact : AppCompatActivity() {

    private lateinit var contactNumberEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_contact)

        contactNumberEditText = findViewById(R.id.contactNumberEditText)

        findViewById<Button>(R.id.saveContactButton).setOnClickListener {
            saveContact(contactNumberEditText.text.toString())
        }

        findViewById<Button>(R.id.deleteContactButton).setOnClickListener {
            deleteContact()
        }

        displayExistingContact()
    }

    private fun saveContact(contactNumber: String) {
        val sharedPrefs = getSharedPreferences("SOS_Cont", MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString("emergency_contact", contactNumber)
            apply()
        }
        Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT).show()

        // Navigate back to the main SOSActivity
        val intent = Intent(this, HomePage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun displayExistingContact() {
        val sharedPrefs = getSharedPreferences("SOS_Cont", MODE_PRIVATE)
        val contactNumber = sharedPrefs.getString("emergency_contact", null)
        contactNumber?.let {
            contactNumberEditText.setText(it)
        }
    }

    private fun deleteContact() {
        val sharedPrefs = getSharedPreferences("SOS_Cont", MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            remove("emergency_contact")
            apply()
        }
        contactNumberEditText.setText("")
        Toast.makeText(this, "Contact deleted, Please add Another Contact", Toast.LENGTH_LONG).show()
    }
}
