package com.se46.guardia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.se46.guardia.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener{
            val eMail = binding.mailText.text.toString()
            val passWord = binding.passText.text.toString()

            if(eMail.isNotEmpty() && passWord.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(eMail,passWord).addOnCompleteListener{
                        if(it.isSuccessful){
                            val intentOfRegister = Intent(this,HomePage::class.java)
                            startActivity(intentOfRegister)
                        }
                        else{
                            Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else{
                Toast.makeText(this, "Empty fields are not Allowed !!  ", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun call_register(view: View) {
        val intent = Intent(this, RegisterPage::class.java)
        startActivity(intent)
    }
}