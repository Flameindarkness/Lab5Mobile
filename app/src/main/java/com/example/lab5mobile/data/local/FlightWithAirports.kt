package com.example.lab5mobile.data.local

/**
 * Результат запроса с JOIN: рейс вместе с данными аэропортов и признаком избранного
 */
data class FlightWithAirports(
    val flightNumber: String?,
    val departureCode: String,
    val departureName: String,
    val arrivalCode: String,
    val arrivalName: String,
    val departureTime: String?,
    val arrivalTime: String?,
    val isFavorite: Boolean
)
