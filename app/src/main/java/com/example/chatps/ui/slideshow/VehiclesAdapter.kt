package com.example.chatps.ui.slideshow

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatps.databinding.ItemContainerVehicleBinding

class VehiclesAdapter(
    private var vehicles: List<Vehicle> = emptyList(),
    private var vehicleListener: VehicleListener? = null
) : RecyclerView.Adapter<VehiclesAdapter.VehicleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val binding = ItemContainerVehicleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VehicleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.bind(vehicles[position])
    }

    override fun getItemCount(): Int = vehicles.size

    inner class VehicleViewHolder(private val binding: ItemContainerVehicleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(vehicle: Vehicle) {
            binding.textMarca.text = vehicle.vehicleMarca
            binding.textPlaca.text = vehicle.vehiclePlaca
            binding.imageProfile.setImageBitmap(vehicle.vehicleImage?.let { getVehicleImage(it) })
            binding.root.setOnClickListener {
                vehicleListener?.onVehicleClicked(vehicle)
            }
        }
    }

    private fun getVehicleImage(encodedImage: String): Bitmap? {
        return try {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}