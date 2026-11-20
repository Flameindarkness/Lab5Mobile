package com.example.lab5mobile.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.lab5mobile.R
import com.example.lab5mobile.databinding.ItemFlightBinding
import com.example.lab5mobile.domain.model.FlightInfo

/**
 * Карточка рейса: маршрут, номер, время и кнопка избранного
 */
class FlightViewHolder(private val binding: ItemFlightBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(flight: FlightInfo, onFavoriteClick: (FlightInfo) -> Unit) {
        val context = binding.root.context
        with(binding) {
            flightNumber.text = context.getString(
                R.string.flight_number_and_time,
                flight.flightNumber,
                flight.departureTime,
                flight.arrivalTime
            )
            departureCode.text = flight.departureCode
            departureName.text = flight.departureName
            arrivalCode.text = flight.arrivalCode
            arrivalName.text = flight.arrivalName

            favoriteButton.setImageResource(
                if (flight.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )
            favoriteButton.contentDescription = context.getString(
                if (flight.isFavorite) R.string.favorite_remove else R.string.favorite_add
            )
            favoriteButton.setOnClickListener { onFavoriteClick(flight) }
        }
    }
}
