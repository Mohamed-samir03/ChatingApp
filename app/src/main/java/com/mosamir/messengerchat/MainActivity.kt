package com.mosamir.messengerchat

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mosamir.messengerchat.fragment.ChatFragment
import com.mosamir.messengerchat.fragment.FriendsFragment
import com.mosamir.messengerchat.fragment.MoreFragment
import com.mosamir.messengerchat.model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    lateinit var myStorage: StorageReference
    val db = Firebase.firestore.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")
    private val chatFragment = ChatFragment()
    private val friendsFragment = FriendsFragment()
    private val moreFragment = MoreFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        myStorage = FirebaseStorage.getInstance().reference
        db.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)
            if(user!!.profileImage.isNotEmpty()){
                var islandRef = myStorage.child(user.profileImage)
                val ONE_MEGABYTE: Long = 5L *1024 *1024
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                    var imageBitmap = BitmapFactory.decodeByteArray(it,0,it.size)
                    img_user.setImageBitmap(imageBitmap)
                }.addOnFailureListener {
                    img_user.setImageResource(R.drawable.account_image)
                }
            }
        }
        setSupportActionBar(toolbar_main)
        supportActionBar?.title = ""
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else{
            window.statusBarColor = Color.WHITE
        }
        bottomNavigationView_main.setOnNavigationItemSelectedListener(this)
        setFragment(chatFragment)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.navigation_chat ->{
                setFragment(chatFragment)
                return true
            }
            R.id.navigation_friends ->{
                setFragment(friendsFragment)
                return true
            }
            R.id.navigation_more ->{
                setFragment(moreFragment)
                return true
            }
            else -> return false
        }
    }

    private fun setFragment(fragment: Fragment) {
        val fr = supportFragmentManager.beginTransaction()
        fr.replace(R.id.coordinatorlayout_main_content, fragment)
        fr.commit()
    }
}