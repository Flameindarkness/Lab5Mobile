package com.example.lab5mobile.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.example.lab5mobile.R
import com.example.lab5mobile.databinding.ItemAirportSuggestionBinding
import com.example.lab5mobile.domain.model.Airport

/**
 * Адаптер выпадающих подсказок. Фильтрация уже выполнена запросом к БД,
 * поэтому стандартный фильтр ArrayAdapter заменён на «пропускающий всё»
 */
class AirportSuggestionAdapter(context: Context) :
    ArrayAdapter<Airport>(context, R.layout.item_airport_suggestion) {

    private var airports: List<Airport> = emptyList()

    private val passThroughFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults =
            FilterResults().apply {
                values = airports
                count = airports.size
            }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence =
            (resultValue as? Airport)?.iataCode.orEmpty()
    }

    fun submitList(newAirports: List<Airport>) {
        airports = newAirports
        clear()
        addAll(newAirports)
    }

    override fun getFilter(): Filter = passThroughFilter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let { ItemAirportSuggestionBinding.bind(it) }
            ?: ItemAirportSuggestionBinding.inflate(LayoutInflater.from(context), parent, false)
        getItem(position)?.let { airport ->
            binding.airportCode.text = airport.iataCode
            binding.airportName.text = airport.name
        }
        return binding.root
    }
}
