package com.example.aplikasisederhana

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewsDetail : AppCompatActivity() {

    lateinit var newsTitle: TextView
    lateinit var newsSubtitle: TextView
    lateinit var newsImage: ImageView

    lateinit var edit: Button
    lateinit var hapus: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mAuth = FirebaseAuth.getInstance()

        newsTitle = findViewById(R.id.newsTitle)
        newsSubtitle = findViewById(R.id.newsSubtitle)
        newsImage = findViewById(R.id.newsImage)
        edit = findViewById(R.id.editButton)
        hapus = findViewById(R.id.deleteButton)
        db = FirebaseFirestore.getInstance()


        val intent = intent
        val id = intent.getStringExtra( "id")
        val title = intent.getStringExtra( "title")
        val subtitle = intent.getStringExtra( "desc")
        val imageUrl = intent.getStringExtra( "imageUrl")


        newsTitle.text = title
        newsSubtitle.text = subtitle
        Glide.with(this).load(imageUrl).into(newsImage)

        edit.setOnClickListener {
            val judulHalaman = "Edit"

            val intent = Intent(this@NewsDetail, NewsAdd::class.java)
            intent.putExtra("id", id)
            intent.putExtra("title", title)
            intent.putExtra("desc", subtitle)
            intent.putExtra("imageUrl", imageUrl)

            intent.putExtra("judulHalaman", judulHalaman)
            startActivity(intent)
        }

        hapus.setOnClickListener {
            confirmationDelete(id!!)
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
            Toast.makeText(this@NewsDetail, "Logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@NewsDetail, MainActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }else if (id == R.id.action_back){
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun confirmationDelete(newsId: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete")
        builder.setMessage("Are you sure want to delete?")
        builder.setPositiveButton("Delete"){ dialog, which ->
            deleteNews(newsId)
        }
        builder.setNegativeButton("Cancel"){ dialog, which ->
            dialog.cancel()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteNews(newsId: String){
        db.collection("news").document(newsId)
            .delete()
            .addOnSuccessListener { aVoid ->
                Toast.makeText(this@NewsDetail, "News deleted successfully",
                    Toast.LENGTH_SHORT).show()
                val intent = Intent( this@NewsDetail, DashboardActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText( this@NewsDetail, "Error deleting news: ${e.message}",
                    Toast.LENGTH_SHORT).show()
                Log.w(  "NewsDetail",  "Error deleting document", e)
            }
    }
}