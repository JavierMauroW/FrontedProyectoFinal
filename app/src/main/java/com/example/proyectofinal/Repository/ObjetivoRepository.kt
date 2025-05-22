package com.example.proyectofinal.Repository


import com.example.proyectofinal.interfaces.RetrofitClient
import com.example.proyectofinal.model.Objetivo

class ObjetivoRepository {
    private val api = RetrofitClient.apiService

    suspend fun getAllObjetivos(): List<Objetivo> =
        api.getAllObjetivos()

    suspend fun createObjetivo(objetivo: Objetivo): Objetivo =
        api.createObjetivo(objetivo)

    suspend fun updateObjetivo(id: Int, objetivo: Objetivo): Objetivo =
        api.updateObjetivo(id, objetivo)

    suspend fun deleteObjetivo(id: Int) {
        api.deleteObjetivo(id)
    }
}

