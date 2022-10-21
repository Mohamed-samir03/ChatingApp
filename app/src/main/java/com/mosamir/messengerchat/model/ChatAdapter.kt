package com.mosamir.messengerchat.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mosamir.messengerchat.R

class ChatAdapter(private var chatList:ArrayList<TextMessage>): RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    private val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView:View
        if(this.getItemViewType(viewType)==0){
            itemView= LayoutInflater.from(parent.context).inflate(R.layout.chat_item,parent,false)
        }else{
            itemView= LayoutInflater.from(parent.context).inflate(R.layout.chat_item_receiver,parent,false)
        }
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentMessage = chatList[position]
        holder.message.text = currentMessage.text
        holder.time.text = android.text.format.DateFormat.format("hh:mm a",currentMessage.date)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        if(chatList[position].senderId == currentUserUid){
            return 0
        }
        return 1
    }

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var message = itemView.findViewById<TextView>(R.id.tv_message_chat)
        var time = itemView.findViewById<TextView>(R.id.tv_time_chat)
    }

}