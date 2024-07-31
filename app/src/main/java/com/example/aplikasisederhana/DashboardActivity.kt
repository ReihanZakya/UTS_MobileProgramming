package com.example.aplikasisederhana

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var myAdapter: AdapterList
    private lateinit var itemList: MutableList<ItemList>
    private lateinit var db: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)


        //Inisialisasi Firebase
        FirebaseApp.initializeApp( this)
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        //Inisialisasi UI komponen
        progressDialog = ProgressDialog( this@DashboardActivity).apply {
            setTitle("Loading...")
        }
        val recyclerView = findViewById<RecyclerView>(R.id.rcvNews)
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatAddNews)

        //Setup RecylerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager( this)
        itemList = ArrayList()
        myAdapter = AdapterList(itemList)
        recyclerView.adapter = myAdapter


        floatingActionButton.setOnClickListener {
            val judulHalaman = "Add New Data"

            val intent= Intent(this, NewsAdd::class.java)

            intent.putExtra("judulHalaman", judulHalaman)
            startActivity(intent)
        }

        myAdapter.setOnItemClickListener(object: AdapterList.OnItemClickListener{
            override fun onItemClick(item: ItemList) {
                val intent = Intent(this@DashboardActivity, NewsDetail::class.java).apply {
                    putExtra("id", item.id)
                    putExtra("title", item.judul)
                    putExtra("desc", item.subJudul)
                    putExtra("imageUrl", item.imageUrl)
                }
                startActivity(intent)
            }
        })

    }


    override fun onStart(){
        super.onStart()
        getData()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        progressDialog.show()
        db.collection("news")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    itemList.clear()
                    for (document in task.result) {
                        val item = ItemList(
                            document.id,
                            document.getString("title") ?: "",
                            document.getString("desc") ?: "",
                            document.getString("imageUrl") ?: ""
                        )
                        itemList.add(item)
                        Log.d("data", "${document.id} => ${document.data}")
                    }
                    myAdapter.notifyDataSetChanged()
                } else {
                    Log.w("data", "Error getting documents.", task.exception)
                }
                progressDialog.dismiss()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_logout){
            mAuth.signOut()
            Toast.makeText(this@DashboardActivity, "Logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@DashboardActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }else if (id == R.id.action_back){
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}