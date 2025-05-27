package com.example.proyectofinal.Repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.interfaces.RetrofitClient
import com.example.proyectofinal.model.Objetivo

class ObjetivoRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    private val api = RetrofitClient.apiService

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllObjetivos(): List<Objetivo> =
        api.getAllObjetivos()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createObjetivo(objetivo: Objetivo): Objetivo =
        api.createObjetivo(objetivo)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateObjetivo(id: Int, objetivo: Objetivo): Objetivo =
        api.updateObjetivo(id, objetivo)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteObjetivo(id: Int) {
        api.deleteObjetivo(id)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun marcarObjetivoComoCompletado(id: Int): Objetivo =
        api.marcarObjetivoComoCompletado(id)
}