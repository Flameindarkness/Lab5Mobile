package com.example.lab5mobile.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.lab5mobile.domain.repository.SearchPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Хранит последний поисковый запрос в Preferences DataStore
 */
class SearchPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SearchPreferencesRepository {

    override val lastAirportCode: Flow<String?> = dataStore.data
        .catch { exception ->
            // При ошибке чтения файла считаем, что сохранённого запроса нет
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences -> preferences[LAST_AIRPORT_CODE] }

    override suspend fun saveLastAirportCode(code: String?) {
        dataStore.edit { preferences ->
            if (code.isNullOrBlank()) {
                preferences.remove(LAST_AIRPORT_CODE)
            } else {
                preferences[LAST_AIRPORT_CODE] = code
            }
        }
    }

    private companion object {
        val LAST_AIRPORT_CODE = stringPreferencesKey("last_airport_code")
    }
}
