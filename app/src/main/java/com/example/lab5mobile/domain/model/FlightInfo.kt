package com.example.lab5mobile.domain.model

/**
 * Рейс для отображения в списке
 * @param flightNumber Номер рейса
 * @param departureCode Код IATA аэропорта вылета
 * @param departureName Название аэропорта вылета
 * @param arrivalCode Код IATA аэропорта прибытия
 * @param arrivalName Название аэропорта прибытия
 * @param departureTime Время вылета (ЧЧ:ММ)
 * @param arrivalTime Время прилёта (ЧЧ:ММ)
 * @param isFavorite Рейс в избранном
 */
data class FlightInfo(
    val flightNumber: String,
    val departureCode: String,
    val departureName: String,
    val arrivalCode: String,
    val arrivalName: String,
    val departureTime: String,
    val arrivalTime: String,
    val isFavorite: Boolean
)
