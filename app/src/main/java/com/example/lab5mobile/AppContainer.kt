package com.example.lab5mobile

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.lab5mobile.data.local.FlightDatabase
import com.example.lab5mobile.data.preferences.SearchPreferencesRepositoryImpl
import com.example.lab5mobile.data.repository.FlightRepositoryImpl
import com.example.lab5mobile.domain.repository.FlightRepository
import com.example.lab5mobile.domain.repository.SearchPreferencesRepository

private val Context.searchDataStore by preferencesDataStore(name = "search_preferences")

/**
 * Простой контейнер зависимостей: создаёт репозитории один раз на всё приложение
 */
class AppContainer(context: Context) {

    val flightRepository: FlightRepository by lazy {
        FlightRepositoryImpl(FlightDatabase.getInstance(context))
    }

    val searchPreferencesRepository: SearchPreferencesRepository by lazy {
        SearchPreferencesRepositoryImpl(context.searchDataStore)
    }
}
