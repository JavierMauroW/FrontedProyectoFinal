package com.example.proyectofinal.Repository



import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.interfaces.RetrofitClient
import com.example.proyectofinal.model.ProgresoObjetivo

class ProgresoObjetivoRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    private val api = RetrofitClient.apiService

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllProgresos(): List<ProgresoObjetivo> =
        api.getAllProgresos()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createProgreso(progreso: ProgresoObjetivo): ProgresoObjetivo =
        api.createProgreso(progreso)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateProgreso(id: Int, progreso: ProgresoObjetivo): ProgresoObjetivo =
        api.updateProgreso(id, progreso)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteProgreso(id: Int) {
        api.deleteProgreso(id)
    }
}
