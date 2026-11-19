package com.example.lab5mobile.data.local

/**
 * В готовой базе нет таблицы рейсов, поэтому при первом запуске создаются рейсы
 * между всеми парами аэропортов с условным расписанием
 */
object FlightScheduleGenerator {

    private const val MINUTES_IN_DAY = 24 * 60

    fun generate(airports: List<AirportEntity>): List<FlightEntity> {
        val flights = mutableListOf<FlightEntity>()
        for (departure in airports) {
            for (arrival in airports) {
                if (departure.id == arrival.id) continue
                // Время вылета кратно 15 минутам, длительность от 1 до 4,5 часа
                val departureMinutes = (departure.id * 37 + arrival.id * 53) % 96 * 15
                val durationMinutes = 60 + (departure.id + arrival.id) * 17 % 8 * 30
                flights += FlightEntity(
                    flightNumber = "FS %d".format(100 + flights.size + 1),
                    departureAirportId = departure.id,
                    arrivalAirportId = arrival.id,
                    departureTime = formatTime(departureMinutes),
                    arrivalTime = formatTime((departureMinutes + durationMinutes) % MINUTES_IN_DAY)
                )
            }
        }
        return flights
    }

    private fun formatTime(minutes: Int): String = "%02d:%02d".format(minutes / 60, minutes % 60)
}
