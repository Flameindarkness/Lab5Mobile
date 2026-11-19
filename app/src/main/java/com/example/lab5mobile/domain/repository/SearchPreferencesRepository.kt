package com.example.lab5mobile.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Хранение последнего поискового запроса
 */
interface SearchPreferencesRepository {

    /** Код IATA последнего выбранного аэропорта или null */
    val lastAirportCode: Flow<String?>

    /** Сохранить код аэропорта; null — очистить */
    suspend fun saveLastAirportCode(code: String?)
}
