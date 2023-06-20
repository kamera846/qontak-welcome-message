package com.example.qontak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qontak.R
import com.example.qontak.model.ChatModel

class ListChatRecyclerViewAdapter(private val chatList: ArrayList<ChatModel>) : RecyclerView.Adapter<ListChatRecyclerViewAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val ivContactProfile: ImageView = itemView.findViewById(R.id.iv_contact_profile)
        private val tvContactName: TextView = itemView.findViewById(R.id.tv_contact_name)
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message)

        fun bind(chatItem: ChatModel) {
            // Set data to the views
//            ivContactProfile.setImageResource(chatItem.profileImage)
            tvContactName.text = chatItem.title
            tvMessage.text = chatItem.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_room, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatList[position]
        holder.bind(chatItem)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}