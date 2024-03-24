package com.se46.guardia

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class HomePage : AppCompatActivity() {


    private var countdownTimer: CountDownTimer? = null
    private var countdownDialog: Dialog? = null
    private val permissionsRequestCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)
        checkAndRequestPermissions()

        findViewById<Button>(R.id.manageContactButton).setOnClickListener {
            val intent = Intent(this, ManageContact::class.java)
            startActivity(intent)
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val listPermissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), permissionsRequestCode)
            return false // Permissions not granted yet.
        }
        return true // All necessary permissions are already granted.
    }

    private fun showCountdownDialog() {
        countdownDialog = Dialog(this).apply {
            setContentView(R.layout.countdown_dialog)
            setCancelable(false)
            val cancelButton: Button = findViewById(R.id.cancelButton)
            val timerTextView: TextView = findViewById(R.id.timerTextView)
            cancelButton.setOnClickListener {
                dismissCountdownDialog()
            }
            countdownTimer = object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timerTextView.text = (millisUntilFinished / 1000).toString()
                }

                override fun onFinish() {
                    dismissCountdownDialog()
                    startSOSActivity()
                }
            }.start()
            show()
        }
    }

    fun checkAndHandleEmergencyContact(view: View) {
        checkAndHandleEmergencyContact()
    }

    private fun checkAndHandleEmergencyContact() {
        val contact = getSharedPreferences("SOS_Cont", MODE_PRIVATE)
            .getString("emergency_contact", null)
        if (contact.isNullOrEmpty()) {
            // No contact found, redirect to ManageContactActivity
            Toast.makeText(this, "Please set an emergency contact first", Toast.LENGTH_LONG).show()
            val intent = Intent(this, ManageContact::class.java)
            startActivity(intent)
        }
        else
            showCountdownDialog()
    }

    private fun dismissCountdownDialog() {
        countdownTimer?.cancel()
        countdownDialog?.dismiss()
    }

    private fun startSOSActivity() {
        val intent = Intent(this, SOSActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissCountdownDialog()
    }
}
