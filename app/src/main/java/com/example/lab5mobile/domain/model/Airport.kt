package com.example.lab5mobile.domain.model

/**
 * Аэропорт
 * @param id Идентификатор
 * @param iataCode Трёхбуквенный код IATA
 * @param name Полное название
 */
data class Airport(
    val id: Int,
    val iataCode: String,
    val name: String
)
