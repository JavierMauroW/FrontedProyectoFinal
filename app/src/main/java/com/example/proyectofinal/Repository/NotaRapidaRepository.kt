package com.example.proyectofinal.Repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.interfaces.RetrofitClient
import com.example.proyectofinal.model.NotaRapida

class NotaRapidaRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    private val apiService = RetrofitClient.apiService

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllNotas(): List<NotaRapida> =
        apiService.getAllNotas()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createNota(nota: NotaRapida): NotaRapida =
        apiService.createNota(nota)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateNota(notaOriginal: NotaRapida, notaNueva: NotaRapida): NotaRapida =
        apiService.updateNota(notaOriginal.idNota, notaNueva)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteNota(id: Int) {
        apiService.deleteNota(id)
    }
}