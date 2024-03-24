package com.se46.guardia

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.concurrent.Executor

class SOSActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val permissionsRequestCode = 101
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sos_activity)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startRepeatingSOSTimer()

        // Initialize biometric authentication.
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    cancelSOS()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate Your Fingerprint to cancel SOS!")
            .setNegativeButtonText(" ")
            .build()
    }

    private fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    private fun startRepeatingSOSTimer() {
        countdownTimer = object : CountDownTimer(300000, 300000) { // 300000ms is 5 minutes
            override fun onTick(millisUntilFinished: Long) {
                sendSOSMessage()
            }

            override fun onFinish() {
                start() // Restart the timer for another 5 minutes.
            }
        }.start()
    }

    private fun sendSOSMessage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION), permissionsRequestCode)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    // Fetch the stored contact number
                    val sharedPrefs = getSharedPreferences("SOS_Cont", MODE_PRIVATE)
                    val emergencyContact = sharedPrefs.getString("emergency_contact", null)

                    val smsManager = SmsManager.getDefault()
                    val message = "Emergency! I need help. My location: https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
                    smsManager.sendTextMessage(emergencyContact, null, message, null, null)
                    Toast.makeText(applicationContext, "SOS message sent to $emergencyContact.", Toast.LENGTH_LONG).show()
                } ?: Toast.makeText(applicationContext, "Failed to get current location.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun cancelSOS(view: View) {
        if (isBiometricAvailable()) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            // Skip biometric authentication and directly cancel SOS if no biometric features are available.
            cancelSOS()
        }
    }

    private fun cancelSOS() {
        countdownTimer?.cancel()
        Toast.makeText(this, "SOS Cancelled", Toast.LENGTH_SHORT).show()
        finish() // Close the activity
    }
}
