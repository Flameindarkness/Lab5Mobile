package com.example.lab5mobile.domain.repository

import com.example.lab5mobile.domain.model.Airport
import com.example.lab5mobile.domain.model.FlightInfo
import kotlinx.coroutines.flow.Flow

/**
 * Доступ к аэропортам, рейсам и избранному
 */
interface FlightRepository {

    /** Подсказки: аэропорты, код или название которых совпадает с запросом */
    fun searchAirports(query: String): Flow<List<Airport>>

    /** Аэропорт по коду IATA или по началу названия, null — если не найден */
    suspend fun findAirport(query: String): Airport?

    /** Рейсы из аэропорта с указанным кодом */
    fun getFlightsFrom(airportCode: String): Flow<List<FlightInfo>>

    /** Все избранные рейсы */
    fun getFavoriteFlights(): Flow<List<FlightInfo>>

    /** Добавить рейс в избранное или удалить из него */
    suspend fun toggleFavorite(flight: FlightInfo)

    /** Заполняет таблицу рейсов при первом запуске */
    suspend fun prepareFlights()
}
