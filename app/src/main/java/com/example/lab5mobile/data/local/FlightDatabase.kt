package com.example.lab5mobile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * База данных Room. При первом запуске копируется из assets/database/flight_search.db
 */
@Database(
    entities = [AirportEntity::class, FavoriteEntity::class, FlightEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FlightDatabase : RoomDatabase() {

    abstract fun airportDao(): AirportDao
    abstract fun flightDao(): FlightDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        private const val DATABASE_NAME = "flight_search.db"
        private const val ASSET_PATH = "database/flight_search.db"

        @Volatile
        private var instance: FlightDatabase? = null

        fun getInstance(context: Context): FlightDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, FlightDatabase::class.java, DATABASE_NAME)
                    .createFromAsset(ASSET_PATH)
                    .build()
                    .also { instance = it }
            }
    }
}
