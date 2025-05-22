package com.example.proyectofinal.Repository





import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.interfaces.RetrofitClient
import com.example.proyectofinal.model.NotaRapida

class NotaRapidaRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    private val api = RetrofitClient.apiService

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllNotas(): List<NotaRapida> =
        api.getAllNotas()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createNota(nota: NotaRapida): NotaRapida =
        api.createNota(nota)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateNota(id: NotaRapida, nota: NotaRapida): NotaRapida =
        api.updateNota(id, nota)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteNota(id: Int) {
        api.deleteNota(id)
    }
}