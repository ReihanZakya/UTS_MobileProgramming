package com.example.aplikasisederhana

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        var btnRegisterClick = findViewById<Button>(R.id.btnRegister)

        var textName = findViewById<EditText>(R.id.name)
        var textEmail = findViewById<EditText>(R.id.email)
        var textPassword = findViewById<EditText>(R.id.password)


        btnRegisterClick.setOnClickListener(){

            var name = textName.text.toString()
            var email = textEmail.text.toString()
            var password = textPassword.text.toString()

            if(name.isEmpty()){
                textName.setError("Name is required")
            }
            if(email.isEmpty()){
                textEmail.setError("Email is required")
            }
            if (password.isEmpty()){
                textPassword.setError("Password is required")
            }

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
                Toast.makeText(this, "Thank You For Registration", Toast.LENGTH_LONG).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
            }
        }


        var clickToLogin = findViewById<TextView>(R.id.clickToLogin)

        clickToLogin.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}