package com.example.lab5mobile

import android.app.Application

/**
 * Класс приложения, хранит контейнер зависимостей
 */
class FlightSearchApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
