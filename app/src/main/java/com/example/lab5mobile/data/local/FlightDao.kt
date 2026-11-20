package com.example.lab5mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {

    @Query("SELECT COUNT(*) FROM flight")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(flights: List<FlightEntity>)

    /**
     * Рейсы из аэропорта. Flow выдаёт новый список при любом изменении таблиц
     * flight, airport и favorite — поэтому «сердечко» обновляется сразу
     */
    @Query(
        """
        SELECT f.flight_number AS flightNumber,
               d.iata_code AS departureCode, d.name AS departureName,
               a.iata_code AS arrivalCode, a.name AS arrivalName,
               f.departure_time AS departureTime, f.arrival_time AS arrivalTime,
               EXISTS(
                   SELECT 1 FROM favorite fav
                   WHERE fav.departure_code = d.iata_code AND fav.destination_code = a.iata_code
               ) AS isFavorite
        FROM flight f
        JOIN airport d ON f.departure_airport_id = d.id
        JOIN airport a ON f.arrival_airport_id = a.id
        WHERE UPPER(d.iata_code) = UPPER(:airportCode)
        ORDER BY f.departure_time
        """
    )
    fun getFlightsFrom(airportCode: String): Flow<List<FlightWithAirports>>

    /** Все избранные рейсы в порядке добавления */
    @Query(
        """
        SELECT f.flight_number AS flightNumber,
               d.iata_code AS departureCode, d.name AS departureName,
               a.iata_code AS arrivalCode, a.name AS arrivalName,
               f.departure_time AS departureTime, f.arrival_time AS arrivalTime,
               1 AS isFavorite
        FROM favorite fav
        JOIN airport d ON d.iata_code = fav.departure_code
        JOIN airport a ON a.iata_code = fav.destination_code
        LEFT JOIN flight f ON f.departure_airport_id = d.id AND f.arrival_airport_id = a.id
        ORDER BY fav.id
        """
    )
    fun getFavoriteFlights(): Flow<List<FlightWithAirports>>
}
