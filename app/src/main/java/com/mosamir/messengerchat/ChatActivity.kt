package com.mosamir.messengerchat

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mosamir.messengerchat.model.ChatAdapter
import com.mosamir.messengerchat.model.TextMessage
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var myStorage: StorageReference
    private val db = Firebase.firestore
    private val chatChannelsCollectionRef = db.collection("chatChannels")
    private val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
    private var userUid:String =""
    private lateinit var textMessageList:ArrayList<TextMessage>
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var adapter:ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else{
            window.statusBarColor = Color.WHITE
        }
        imageView_back.setOnClickListener {
            finish()
        }
        myStorage = FirebaseStorage.getInstance().reference
        textMessageList = ArrayList<TextMessage>()
        val name = intent.getStringExtra("name")
        val image = intent.getStringExtra("image")
        userUid = intent.getStringExtra("uid").toString()
        tv_friend_name_chat.text = name
        if(image!!.isNotEmpty()){
            var islandRef = myStorage.child(image)
            val ONE_MEGABYTE: Long = 5L *1024 *1024
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                var imageBitmap = BitmapFactory.decodeByteArray(it,0,it.size)
                img_friend_chat.setImageBitmap(imageBitmap)
            }.addOnFailureListener {
                img_friend_chat.setImageResource(R.drawable.account_image)
            }
        }
        createChatChannel(){channelId ->
            getMessage(channelId)
            img_send.setOnClickListener {
                val messageSend = TextMessage(et_send_message.text.toString(),currentUserUid.toString(),userUid,Calendar.getInstance().time)
                if(et_send_message.text.trim().toString().isNotEmpty()){
                    sendMessage(channelId,messageSend)
                    et_send_message.setText("")
                    textMessageList = ArrayList<TextMessage>()
                    getMessage(channelId)
                }
            }
        }


    }

    private fun sendMessage(channelId: String,messageSend: TextMessage) {
        chatChannelsCollectionRef.document(channelId).collection("message").add(messageSend)
    }

    private fun createChatChannel(onComplete:(channelId:String)->Unit) {

        db.collection("users")
            .document(currentUserUid.toString())
            .collection("chatChannel")
            .document(userUid)
            .get().addOnSuccessListener {
                if(it.exists()){
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }

                val newChatChannel = db.collection("users").document()

                db.collection("users")
                    .document(userUid)
                    .collection("chatChannel")
                    .document(currentUserUid!!)
                    .set(mapOf("channelId" to newChatChannel.id))

                db.collection("users")
                    .document(currentUserUid)
                    .collection("chatChannel")
                    .document(userUid!!)
                    .set(mapOf("channelId" to newChatChannel.id))

                onComplete(newChatChannel.id)

            }

    }

    fun getMessage(channelId: String){
        val query = chatChannelsCollectionRef.document(channelId).collection("message")
        query.orderBy("date",com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get().addOnSuccessListener {
                for (i in it){
                    val tMessage = i.toObject<TextMessage>()
                    textMessageList.add(tMessage)
                }
                messagesRecyclerView = findViewById(R.id.chat_recyclerView)
                adapter = ChatAdapter(textMessageList)
                messagesRecyclerView.adapter = adapter
                messagesRecyclerView.hasFixedSize()
        }
    }

}