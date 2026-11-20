package com.example.lab5mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lab5mobile.FlightSearchApplication
import com.example.lab5mobile.R
import com.example.lab5mobile.domain.model.Airport
import com.example.lab5mobile.domain.model.FlightInfo
import com.example.lab5mobile.domain.repository.FlightRepository
import com.example.lab5mobile.domain.repository.SearchPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel главного экрана: поиск аэропорта, подсказки, рейсы и избранное.
 * Все данные отдаются через StateFlow, поэтому экран обновляется автоматически
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class FlightSearchViewModel(
    private val flightRepository: FlightRepository,
    private val searchPreferences: SearchPreferencesRepository
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    // Код выбранного аэропорта; null — показываем избранное
    private val selectedAirportCode = MutableStateFlow<String?>(null)

    // false, пока не подготовлена база и не прочитан сохранённый запрос
    private val isReady = MutableStateFlow(false)

    private val _messages = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val messages: SharedFlow<Int> = _messages.asSharedFlow()

    /** Подсказки для поля ввода (запрос к БД через LIKE) */
    val suggestions: StateFlow<List<Airport>> = _searchText
        .debounce(SUGGESTIONS_DELAY_MS)
        .flatMapLatest { text ->
            val query = text.trim()
            if (query.isEmpty() || query.equals(selectedAirportCode.value, ignoreCase = true)) {
                flowOf(emptyList())
            } else {
                flightRepository.searchAirports(query).catch { emit(emptyList()) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    /** Список рейсов: избранное или результаты поиска */
    val uiState: StateFlow<FlightsUiState> = combine(isReady, selectedAirportCode) { ready, code ->
        ready to code
    }
        .flatMapLatest { (ready, code) ->
            when {
                !ready -> flowOf(FlightsUiState.Loading)
                code == null -> flightRepository.getFavoriteFlights()
                    .map<List<FlightInfo>, FlightsUiState> { FlightsUiState.Favorites(it) }

                else -> flightRepository.getFlightsFrom(code)
                    .map<List<FlightInfo>, FlightsUiState> { FlightsUiState.SearchResults(code, it) }
            }.catch { emit(FlightsUiState.Error(R.string.error_database)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), FlightsUiState.Loading)

    init {
        viewModelScope.launch {
            try {
                flightRepository.prepareFlights()
                // Восстанавливаем последний запрос из DataStore и сразу выполняем поиск
                val savedCode = searchPreferences.lastAirportCode.first()
                if (!savedCode.isNullOrBlank()) {
                    _searchText.value = savedCode
                    selectedAirportCode.value = savedCode
                }
            } catch (e: Exception) {
                _messages.tryEmit(R.string.error_database)
            } finally {
                isReady.value = true
            }
        }
    }

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
        if (text.isBlank()) {
            // Поле очищено — возвращаемся к избранному и забываем запрос
            selectedAirportCode.value = null
            viewModelScope.launch { searchPreferences.saveLastAirportCode(null) }
        }
    }

    fun onAirportSelected(airport: Airport) {
        selectAirport(airport.iataCode)
    }

    /** Кнопка «Найти»: ищем аэропорт по коду или по началу названия */
    fun onSearch() {
        val query = _searchText.value.trim()
        if (query.isEmpty()) return
        viewModelScope.launch {
            try {
                val airport = flightRepository.findAirport(query)
                if (airport != null) {
                    selectAirport(airport.iataCode)
                } else {
                    _messages.emit(R.string.error_airport_not_found)
                }
            } catch (e: Exception) {
                _messages.emit(R.string.error_database)
            }
        }
    }

    fun onFavoriteClick(flight: FlightInfo) {
        viewModelScope.launch {
            try {
                flightRepository.toggleFavorite(flight)
            } catch (e: Exception) {
                _messages.emit(R.string.error_database)
            }
        }
    }

    private fun selectAirport(code: String) {
        _searchText.value = code
        selectedAirportCode.value = code
        viewModelScope.launch { searchPreferences.saveLastAirportCode(code) }
    }

    companion object {
        private const val SUGGESTIONS_DELAY_MS = 250L
        private const val STOP_TIMEOUT_MS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as FlightSearchApplication).container
                FlightSearchViewModel(container.flightRepository, container.searchPreferencesRepository)
            }
        }
    }
}
