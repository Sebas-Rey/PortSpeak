package com.example.chatps.ui.llamadas

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatps.databinding.ItemContainerLlamadasBinding
import com.example.chatps.ui.chat.listeners.UserListener
import com.example.chatps.ui.chat.models.User

class LlamadasAdapter(
    private val users: List<User>,
    private val userListener: UserListener
) : RecyclerView.Adapter<LlamadasAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemContainerLlamadasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(private val binding: ItemContainerLlamadasBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.textName.text = user.name
            binding.textPhone.text = user.phone
            binding.imageProfile.setImageBitmap(user.image?.let { getUserImage(it) })
            binding.root.setOnClickListener { userListener.onUserClicked(user) }
        }
    }

    private fun getUserImage(encodedImage: String): Bitmap {
        return try {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            // Handle error or return a placeholder image
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Placeholder image
        }
    }
}