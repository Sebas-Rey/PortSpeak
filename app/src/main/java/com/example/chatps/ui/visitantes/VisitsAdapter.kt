package com.example.chatps.ui.visitantes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatps.databinding.ItemContainerVisitBinding

class VisitsAdapter(
    private var visits: List<Visit> = emptyList(),
    private var visitListener: VisitListener? = null
) : RecyclerView.Adapter<VisitsAdapter.VisitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val binding = ItemContainerVisitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VisitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        holder.bind(visits[position])
    }

    override fun getItemCount(): Int = visits.size

    inner class VisitViewHolder(private val binding: ItemContainerVisitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(visit: Visit) {
            binding.visitName.text = visit.visitName
            binding.visitPhone.text = visit.visitPhone
            binding.visitTimeEnter.text = visit.visitTimeEnter
            binding.root.setOnClickListener {
                visitListener?.onVisitClicked(visit)
            }
        }
    }
}