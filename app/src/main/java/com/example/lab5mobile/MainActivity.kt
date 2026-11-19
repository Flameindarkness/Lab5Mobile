package com.example.lab5mobile

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.lab5mobile.databinding.ActivityMainBinding
import com.example.lab5mobile.domain.model.FlightInfo
import com.example.lab5mobile.ui.AirportSuggestionAdapter
import com.example.lab5mobile.ui.FlightAdapter
import com.example.lab5mobile.ui.FlightSearchViewModel
import com.example.lab5mobile.ui.FlightsUiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Главный экран: поле поиска с подсказками и список рейсов
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: FlightSearchViewModel by viewModels { FlightSearchViewModel.Factory }

    private lateinit var suggestionAdapter: AirportSuggestionAdapter
    private val flightAdapter = FlightAdapter { flight -> viewModel.onFavoriteClick(flight) }

    // true, пока текст в поле меняет сама программа, а не пользователь
    private var isUpdatingText = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val safeArea = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout() or
                    WindowInsetsCompat.Type.ime()
            )
            v.setPadding(safeArea.left, safeArea.top, safeArea.right, safeArea.bottom)
            insets
        }

        setupSearchField()
        binding.flightList.adapter = flightAdapter
        observeViewModel()
    }

    private fun setupSearchField() {
        suggestionAdapter = AirportSuggestionAdapter(this)
        with(binding.searchInput) {
            setAdapter(suggestionAdapter)
            threshold = 1
            doAfterTextChanged { text ->
                if (!isUpdatingText) viewModel.onSearchTextChanged(text?.toString().orEmpty())
            }
            setOnItemClickListener { _, _, position, _ ->
                suggestionAdapter.getItem(position)?.let { airport ->
                    viewModel.onAirportSelected(airport)
                    hideKeyboard()
                }
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.onSearch()
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }
        }
        binding.searchButton.setOnClickListener {
            viewModel.onSearch()
            hideKeyboard()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchText.collect { text ->
                        if (binding.searchInput.text.toString() != text) {
                            isUpdatingText = true
                            binding.searchInput.setText(text, false)
                            binding.searchInput.setSelection(text.length)
                            isUpdatingText = false
                        }
                    }
                }
                launch {
                    viewModel.suggestions.collect { airports ->
                        suggestionAdapter.submitList(airports)
                        if (airports.isNotEmpty() && binding.searchInput.hasFocus()) {
                            binding.searchInput.showDropDown()
                        } else {
                            binding.searchInput.dismissDropDown()
                        }
                    }
                }
                launch { viewModel.uiState.collect(::render) }
                launch {
                    viewModel.messages.collect { messageRes ->
                        Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun render(state: FlightsUiState) {
        binding.progress.isVisible = state is FlightsUiState.Loading
        when (state) {
            FlightsUiState.Loading -> showList(title = null, flights = emptyList(), emptyText = null)

            is FlightsUiState.Favorites -> showList(
                title = getString(R.string.title_favorites),
                flights = state.flights,
                emptyText = getString(R.string.empty_favorites)
            )

            is FlightsUiState.SearchResults -> showList(
                title = getString(R.string.title_flights_from, state.airportCode),
                flights = state.flights,
                emptyText = getString(R.string.empty_flights)
            )

            is FlightsUiState.Error -> showList(
                title = null,
                flights = emptyList(),
                emptyText = getString(state.messageRes)
            )
        }
    }

    private fun showList(title: String?, flights: List<FlightInfo>, emptyText: String?) {
        binding.listTitle.isVisible = title != null
        binding.listTitle.text = title
        flightAdapter.submitList(flights)
        binding.emptyText.isVisible = flights.isEmpty() && emptyText != null
        binding.emptyText.text = emptyText
    }

    private fun hideKeyboard() {
        binding.searchInput.dismissDropDown()
        getSystemService(InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }
}
