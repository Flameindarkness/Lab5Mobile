package com.example.lab5mobile.ui

import androidx.annotation.StringRes
import com.example.lab5mobile.domain.model.FlightInfo

/**
 * Состояние списка рейсов на главном экране
 */
sealed interface FlightsUiState {

    /** Идёт загрузка */
    data object Loading : FlightsUiState

    /** Поле поиска пустое — показываем избранное */
    data class Favorites(val flights: List<FlightInfo>) : FlightsUiState

    /** Рейсы из выбранного аэропорта */
    data class SearchResults(val airportCode: String, val flights: List<FlightInfo>) : FlightsUiState

    /** Ошибка чтения базы данных */
    data class Error(@StringRes val messageRes: Int) : FlightsUiState
}
