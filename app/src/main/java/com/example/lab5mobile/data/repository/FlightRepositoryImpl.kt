package com.example.lab5mobile.data.repository

import com.example.lab5mobile.data.local.FavoriteEntity
import com.example.lab5mobile.data.local.FlightDatabase
import com.example.lab5mobile.data.local.FlightScheduleGenerator
import com.example.lab5mobile.data.local.toDomain
import com.example.lab5mobile.domain.model.Airport
import com.example.lab5mobile.domain.model.FlightInfo
import com.example.lab5mobile.domain.repository.FlightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Реализация репозитория на Room. Suspend-функции и Flow из DAO выполняются вне главного потока
 */
class FlightRepositoryImpl(database: FlightDatabase) : FlightRepository {

    private val airportDao = database.airportDao()
    private val flightDao = database.flightDao()
    private val favoriteDao = database.favoriteDao()

    override fun searchAirports(query: String): Flow<List<Airport>> =
        airportDao.search(query).map { airports -> airports.map { it.toDomain() } }

    override suspend fun findAirport(query: String): Airport? =
        (airportDao.findByCode(query) ?: airportDao.findByNamePrefix(query))?.toDomain()

    override fun getFlightsFrom(airportCode: String): Flow<List<FlightInfo>> =
        flightDao.getFlightsFrom(airportCode).map { flights -> flights.map { it.toDomain() } }

    override fun getFavoriteFlights(): Flow<List<FlightInfo>> =
        flightDao.getFavoriteFlights().map { flights -> flights.map { it.toDomain() } }

    override suspend fun toggleFavorite(flight: FlightInfo) {
        if (flight.isFavorite) {
            favoriteDao.delete(flight.departureCode, flight.arrivalCode)
        } else {
            favoriteDao.insert(
                FavoriteEntity(departureCode = flight.departureCode, destinationCode = flight.arrivalCode)
            )
        }
    }

    override suspend fun prepareFlights() {
        if (flightDao.count() == 0) {
            flightDao.insertAll(FlightScheduleGenerator.generate(airportDao.getAll()))
        }
    }
}
