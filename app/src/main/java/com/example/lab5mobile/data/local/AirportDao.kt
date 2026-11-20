package com.example.lab5mobile.data.local

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    /** Поиск по началу кода IATA или по части названия (LIKE) */
    @Query(
        """
        SELECT * FROM airport
        WHERE iata_code LIKE :query || '%' OR name LIKE '%' || :query || '%'
        ORDER BY passengers DESC
        LIMIT 10
        """
    )
    fun search(query: String): Flow<List<AirportEntity>>

    @Query("SELECT * FROM airport WHERE UPPER(iata_code) = UPPER(:code) LIMIT 1")
    suspend fun findByCode(code: String): AirportEntity?

    @Query("SELECT * FROM airport WHERE name LIKE :query || '%' ORDER BY passengers DESC LIMIT 1")
    suspend fun findByNamePrefix(query: String): AirportEntity?

    @Query("SELECT * FROM airport")
    suspend fun getAll(): List<AirportEntity>
}
