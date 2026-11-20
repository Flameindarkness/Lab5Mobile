package com.example.lab5mobile.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Рейс между двумя аэропортами. Связи с таблицей airport задаются внешними ключами
 */
@Entity(
    tableName = "flight",
    foreignKeys = [
        ForeignKey(
            entity = AirportEntity::class,
            parentColumns = ["id"],
            childColumns = ["departure_airport_id"]
        ),
        ForeignKey(
            entity = AirportEntity::class,
            parentColumns = ["id"],
            childColumns = ["arrival_airport_id"]
        )
    ],
    indices = [Index("departure_airport_id"), Index("arrival_airport_id")]
)
data class FlightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "flight_number")
    val flightNumber: String,
    @ColumnInfo(name = "departure_airport_id")
    val departureAirportId: Int,
    @ColumnInfo(name = "arrival_airport_id")
    val arrivalAirportId: Int,
    @ColumnInfo(name = "departure_time")
    val departureTime: String,
    @ColumnInfo(name = "arrival_time")
    val arrivalTime: String
)
