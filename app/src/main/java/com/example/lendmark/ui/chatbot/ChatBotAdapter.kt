package com.example.lendmark.ui.chatbot


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lendmark.R
import com.example.lendmark.data.model.ChatMessage

class ChatBotAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_USER = 1
    private val TYPE_AI = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) TYPE_USER else TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_USER) {
            val view = inflater.inflate(R.layout.item_chat_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_chat_ai, parent, false)
            AiViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]

        if (holder is UserViewHolder) {
            holder.userMsg.text = msg.message
        } else if (holder is AiViewHolder) {
            holder.aiMsg.text = msg.message
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    // User 말풍선
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userMsg: TextView = itemView.findViewById(R.id.tvUserMessage)
    }

    // AI 말풍선
    class AiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val aiMsg: TextView = itemView.findViewById(R.id.tvAiMessage)
    }
}
