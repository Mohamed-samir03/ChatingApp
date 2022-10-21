package com.mosamir.messengerchat.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.mosamir.messengerchat.ChatActivity
import com.mosamir.messengerchat.ProfileActivity
import com.mosamir.messengerchat.R
import com.mosamir.messengerchat.model.Friends
import com.mosamir.messengerchat.model.FriendsAdapter
import com.mosamir.messengerchat.model.User
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_chat.view.*

class ChatFragment : Fragment() {

    private lateinit var friendsRecyclerView:RecyclerView
    private lateinit var friendsArrayList: ArrayList<User>
    val db = Firebase.firestore.collection("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_chat, container, false)
        val tv_title = activity?.findViewById<TextView>(R.id.tv_toolbar_title)
        tv_title?.text = "Chat"

        val img_user = activity?.findViewById<ImageView>(R.id.img_user)
        img_user?.setOnClickListener {
            startActivity(Intent(activity,ProfileActivity::class.java))
        }
        v.progressBar_rv.visibility = View.VISIBLE
        friendsArrayList = ArrayList<User>()
        db.get().addOnSuccessListener {
            for (i in it){
                var user = i.toObject<User>()
                friendsArrayList.add(user)
            }
            friendsRecyclerView = v.findViewById(R.id.friends_recyclerView)
            friendsRecyclerView.layoutManager = LinearLayoutManager(activity)
            friendsRecyclerView.hasFixedSize()
            val adapter = FriendsAdapter(friendsArrayList)
            friendsRecyclerView.adapter = adapter
            v.progressBar_rv.visibility = View.INVISIBLE
            adapter.onItemClickListener(object : FriendsAdapter.onItemClickListener{
                override fun onItemClick(position: Int) {
                    var currentUser = friendsArrayList[position]
                    val intent = Intent(activity,ChatActivity::class.java)
                    intent.putExtra("name",currentUser.name)
                    intent.putExtra("image",currentUser.profileImage)
                    intent.putExtra("uid",currentUser.uid)
                    startActivity(intent)
                }
            })
        }
        return v
    }

}