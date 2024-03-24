package com.se46.guardia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.se46.guardia.databinding.ActivityRegisterPageBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterPage : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterPageBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.registerButton.setOnClickListener{  //storing user data to firebase
            val eMail = binding.mailText.text.toString()
            val passWord = binding.passText.text.toString()
            val rePassWord = binding.rePassText.text.toString()

            if(eMail.isNotEmpty() && passWord.isNotEmpty() && rePassWord.isNotEmpty()){
                if(passWord == rePassWord){
                    firebaseAuth.createUserWithEmailAndPassword(eMail,passWord).addOnCompleteListener{
                        if(it.isSuccessful){
                            val intentOfRegister = Intent(this,LoginPage::class.java)
                            startActivity(intentOfRegister)
                        }
                        else{
                            Toast.makeText(this, it.exception.toString() ,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Password is not Correct !!  ",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Empty fields are not Allowed !!  ",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun call_login(view: View) {
        val intent = Intent(this, LoginPage::class.java)
        startActivity(intent)
    }
}