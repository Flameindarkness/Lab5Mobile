package com.example.lab5mobile.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.lab5mobile.databinding.ItemFlightBinding
import com.example.lab5mobile.domain.model.FlightInfo

/**
 * Список рейсов. ListAdapter сам вычисляет изменения через DiffUtil
 */
class FlightAdapter(
    private val onFavoriteClick: (FlightInfo) -> Unit
) : ListAdapter<FlightInfo, FlightViewHolder>(FlightDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder =
        FlightViewHolder(ItemFlightBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(getItem(position), onFavoriteClick)
    }
}
