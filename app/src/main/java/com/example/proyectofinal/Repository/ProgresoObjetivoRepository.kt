package com.example.proyectofinal.Repository



import com.example.proyectofinal.interfaces.RetrofitClient
import com.example.proyectofinal.model.ProgresoObjetivo

class ProgresoObjetivoRepository {
    private val api = RetrofitClient.apiService

    suspend fun getAllProgresos(): List<ProgresoObjetivo> =
        api.getAllProgresos()

    suspend fun createProgreso(progreso: ProgresoObjetivo): ProgresoObjetivo =
        api.createProgreso(progreso)

    suspend fun updateProgreso(id: Int, progreso: ProgresoObjetivo): ProgresoObjetivo =
        api.updateProgreso(id, progreso)

    suspend fun deleteProgreso(id: Int) {
        api.deleteProgreso(id)
    }
}
