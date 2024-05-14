package com.example.aplikasisederhana

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        var btnLoginClick = findViewById<Button>(R.id.btnLogin)
        var textEmail = findViewById<EditText>(R.id.email)
        var textPassword = findViewById<EditText>(R.id.password)

        btnLoginClick.setOnClickListener(){

            var email = textEmail.text.toString()
            var password = textPassword.text.toString()

            if(email.isEmpty()){
                textEmail.setError("Email is required")
            }
            if (password.isEmpty()){
                textPassword.setError("Password is required")
            }

            if (email.isNotEmpty() && password.isNotEmpty()){
                Toast.makeText(this, "Login Success", Toast.LENGTH_LONG).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
            }

        }

        var clickToRegister = findViewById<TextView>(R.id.clickToRegister)

        clickToRegister.setOnClickListener{
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}