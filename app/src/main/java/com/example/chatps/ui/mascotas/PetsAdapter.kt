package com.example.chatps.ui.mascotas

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatps.databinding.ItemContainerPetBinding

class PetsAdapter(
    private var pets: List<Pet> = emptyList(),
    private var petListener: PetListener? = null
) : RecyclerView.Adapter<PetsAdapter.PetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val binding = ItemContainerPetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(pets[position])
    }

    override fun getItemCount(): Int = pets.size

    inner class PetViewHolder(private val binding: ItemContainerPetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pet: Pet) {
            binding.textName.text = pet.petName
            binding.textRaza.text = pet.petRaza
            binding.imageProfile.setImageBitmap(pet.petImage?.let { getPetImage(it) })
            binding.root.setOnClickListener {
                petListener?.onPetClicked(pet)
            }
        }
    }

    private fun getPetImage(encodedImage: String): Bitmap? {
        return try {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}