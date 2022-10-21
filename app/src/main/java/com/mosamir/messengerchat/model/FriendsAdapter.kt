package com.mosamir.messengerchat.model

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mosamir.messengerchat.R
import kotlinx.android.synthetic.main.activity_main.*

class FriendsAdapter(private var friendsList:ArrayList<User>):RecyclerView.Adapter<FriendsAdapter.MyViewHolder>() {

    lateinit var myStorage: StorageReference
    private lateinit var mlistener:onItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item,parent,false)
        return MyViewHolder(itemView,mlistener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = friendsList[position]
        myStorage = FirebaseStorage.getInstance().reference
        if(currentItem.profileImage.isNotEmpty()){
            var islandRef = myStorage.child(currentItem.profileImage)
            val ONE_MEGABYTE: Long = 5L *1024 *1024
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                var imageBitmap = BitmapFactory.decodeByteArray(it,0,it.size)
                holder.image.setImageBitmap(imageBitmap)
            }.addOnFailureListener {
                holder.image.setImageResource(R.drawable.account_image)
            }
        }
        holder.name.text = currentItem.name
        holder.lastMessage.text = "last message...."
        holder.time.text = "time"
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    class MyViewHolder(itemView:View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView){
        var image = itemView.findViewById<ImageView>(R.id.item_imageView)
        var name = itemView.findViewById<TextView>(R.id.item_name_textView)
        var lastMessage = itemView.findViewById<TextView>(R.id.item_last_message_textView)
        var time = itemView.findViewById<TextView>(R.id.item_time_textView)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    fun onItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
}