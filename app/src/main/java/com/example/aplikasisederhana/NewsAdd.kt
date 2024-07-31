package com.example.aplikasisederhana

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.HashMap

class NewsAdd : AppCompatActivity() {

    var id: String? = ""
    var judul: String? = null
    var deskripsi: String? = null
    var image: String? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private var imageUri: Uri? = null
    private lateinit var title: EditText
    private lateinit var desc: EditText
    private lateinit var imageView: ImageView
    private lateinit var saveNews: Button
    private lateinit var chooseImage: Button
    private lateinit var progressDialog: ProgressDialog
    private lateinit var judulHalaman: TextView
    private lateinit var mAuth: FirebaseAuth

    private lateinit var dbNews: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news_add)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mAuth = FirebaseAuth.getInstance()

        judulHalaman = findViewById(R.id.judulHalaman)
        judulHalaman.text = intent.extras?.getString("judulHalaman")?: "Not Found"

        //Initialize Firebase
        dbNews = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        title = findViewById(R.id.title)
        desc = findViewById(R.id.desc)
        imageView = findViewById(R.id.imageView)
        saveNews = findViewById(R.id.btnAdd)
        chooseImage = findViewById(R.id.btnChooseImage)


        progressDialog = ProgressDialog(this@NewsAdd)
        progressDialog.setTitle("Loading...")

        val updateOption = intent
        if (updateOption!=null){
            id = updateOption.getStringExtra("id")
            judul = updateOption.getStringExtra("title")
            deskripsi = updateOption.getStringExtra("desc")
            image = updateOption.getStringExtra("imageUrl")

            title.setText(judul);
            desc.setText(deskripsi)
            Glide.with(this).load(image).into(imageView)
        }

        chooseImage.setOnClickListener {
            openFileChooser()
        }

        saveNews.setOnClickListener {
            val newsTitle = title.text.toString().trim()
            val newsDesc = desc.text.toString().trim()

            if (newsTitle.isEmpty() || newsDesc.isEmpty()){
                Toast.makeText(this, "Title and description cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressDialog.show()
//            uploadImageToStorage(newsTitle, newsDesc)
            Log.d("NewsAdd", "Progress dialog shown")
            if (imageUri != null) {
                Log.d("NewsAdd", "Uploading image to storage")
                uploadImageToStorage(newsTitle, newsDesc)
            } else {
                Log.d("NewsAdd", "Saving data without new image")
                saveData(newsTitle, newsDesc, image ?: "")
            }
        }

    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null)
        {
            imageUri = data.data!!
        }
        imageView.setImageURI(imageUri);
    }


    private fun uploadImageToStorage(newsTitle: String, newsDesc: String) {

        imageUri?.let { uri ->
            val storageRef =
                storage.reference.child("news_images/" + System.currentTimeMillis() + ".jpg")
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()
                        saveData(newsTitle, newsDesc, imageUrl)
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@NewsAdd,
                        "Failed to upload image: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
        }

    }

    private fun saveData(newsTitle: String, newsDesc: String, imageUrl: String) {
        val news = HashMap<String, Any>()
        news["title"] = newsTitle
        news["desc"] = newsDesc
        news["imageUrl"]= imageUrl

        if (id!=null){
            dbNews.collection("news").document(id?:"")
                .update(news)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@NewsAdd, "News update successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@NewsAdd, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this@NewsAdd, "Error updating news: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.w("NewsAdd", "Error adding document", e)
                }
        }else{
            dbNews.collection("news")
                .add(news)
                .addOnSuccessListener { documentReference ->
                    progressDialog.dismiss()
                    Toast.makeText(this@NewsAdd, "News added successfully", Toast.LENGTH_SHORT).show()
                    title.setText("")
                    desc.setText("")
                    imageView.setImageResource(0) // Clear the ImageView
                    finish()
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this@NewsAdd, "Error adding news: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.w("NewsAdd", "Error adding document", e)
                }
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
            Toast.makeText(this@NewsAdd, "Logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@NewsAdd, MainActivity::class.java)
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