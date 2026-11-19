package com.example.lab5mobile.data.local

import com.example.lab5mobile.domain.model.Airport
import com.example.lab5mobile.domain.model.FlightInfo

/** Преобразование данных слоя Data в модели слоя Domain */

fun AirportEntity.toDomain(): Airport = Airport(id = id, iataCode = iataCode, name = name)

fun FlightWithAirports.toDomain(): FlightInfo = FlightInfo(
    flightNumber = flightNumber ?: NO_DATA,
    departureCode = departureCode,
    departureName = departureName,
    arrivalCode = arrivalCode,
    arrivalName = arrivalName,
    departureTime = departureTime ?: NO_DATA,
    arrivalTime = arrivalTime ?: NO_DATA,
    isFavorite = isFavorite
)

private const val NO_DATA = "—"
