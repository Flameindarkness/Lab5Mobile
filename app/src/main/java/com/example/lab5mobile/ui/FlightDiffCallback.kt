package com.example.lab5mobile.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.lab5mobile.domain.model.FlightInfo

/**
 * Сравнение рейсов для ListAdapter: рейс определяется парой «откуда — куда»
 */
class FlightDiffCallback : DiffUtil.ItemCallback<FlightInfo>() {
    override fun areItemsTheSame(oldItem: FlightInfo, newItem: FlightInfo): Boolean =
        oldItem.departureCode == newItem.departureCode && oldItem.arrivalCode == newItem.arrivalCode

    override fun areContentsTheSame(oldItem: FlightInfo, newItem: FlightInfo): Boolean =
        oldItem == newItem
}
