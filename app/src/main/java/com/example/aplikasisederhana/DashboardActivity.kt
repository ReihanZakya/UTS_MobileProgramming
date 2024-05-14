package com.example.aplikasisederhana

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {

    private lateinit var l: ListView
    private val tutorials = arrayOf(
        "Algorithms", "Data Structures",
        "Languages", "Interview Corner",
        "GATE", "ISRO CS",
        "UGC NET CS", "CS Subjects",
        "Web Technologies"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        l = findViewById(R.id.list)
        val arr = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            tutorials
        )
        l.adapter = arr
    }
}