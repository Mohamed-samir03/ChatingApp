package com.mosamir.messengerchat

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import com.google.firebase.storage.StorageReference
import com.mosamir.messengerchat.model.User
import java.util.*

class ProfileActivity : AppCompatActivity() {

    companion object{
        val RS_CODE =1
    }
    private lateinit var auth: FirebaseAuth
    lateinit var userName:String
    lateinit var myStorage:StorageReference
    lateinit var imageName:String
    val db = Firebase.firestore.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(toolbar_profile)
        supportActionBar?.title = "Me"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else{
            window.statusBarColor = Color.WHITE
        }
        auth = Firebase.auth
        myStorage = FirebaseStorage.getInstance().reference

        getUserInfo {
            userName = it.name
            tv_username_profile.text = it.name
            if(it.profileImage.isNotEmpty()){
                progressBar_profile.visibility = View.VISIBLE
                var islandRef = myStorage.child(it.profileImage)
                val ONE_MEGABYTE: Long = 5L *1024 *1024
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                    var imageBitmap = BitmapFactory.decodeByteArray(it,0,it.size)
                    circleImageView_profile.setImageBitmap(imageBitmap)
                    progressBar_profile.visibility = View.INVISIBLE
                }.addOnFailureListener {
                    circleImageView_profile.setImageResource(R.drawable.account_image)
                }
            }
        }

        circleImageView_profile.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
            }
            startActivityForResult(Intent.createChooser(intent,"Select Image"), RS_CODE)
        }

        button_signOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RS_CODE && resultCode == RESULT_OK && data != null && data.data != null){
            circleImageView_profile.setImageURI(data.data)
            progressBar_profile.visibility = View.VISIBLE
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,20,outputStream)
            val selectedImageBytes = outputStream.toByteArray()
            uploadProfileImage(selectedImageBytes){path ->
                val userFieldMap = mutableMapOf<String,Any>()
                userFieldMap["name"] = userName
                userFieldMap["profileImage"] = path
                db.update(userFieldMap)
            }
        }
    }

    private fun uploadProfileImage(selectedImageBytes: ByteArray, onSuccess:(imagePath:String) -> Unit) {
        val ref = myStorage.child("${FirebaseAuth.getInstance().currentUser?.uid.toString()}/profileImage/${UUID.nameUUIDFromBytes(selectedImageBytes)}")
        imageName = UUID.nameUUIDFromBytes(selectedImageBytes).toString()
        ref.putBytes(selectedImageBytes).addOnCompleteListener{
            if(it.isSuccessful){
                progressBar_profile.visibility = View.INVISIBLE
                onSuccess(ref.path)
            }else{
                Toast.makeText(this,it.exception?.message.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getUserInfo(onComplete:(User)->Unit){
        db.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home ->{
                finish()
                return true
            }
        }
        return false
    }
}