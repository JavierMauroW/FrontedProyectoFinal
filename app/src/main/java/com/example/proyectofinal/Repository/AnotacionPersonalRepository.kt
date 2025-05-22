package com.example.proyectofinal.Repository




import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.interfaces.RetrofitClient
import com.example.proyectofinal.model.AnotacionPersonal

class AnotacionPersonalRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    private val api = RetrofitClient.apiService

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllAnotaciones(): List<AnotacionPersonal> =
        api.getAllAnotaciones()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createAnotacion(anotacion: AnotacionPersonal): AnotacionPersonal =
        api.createAnotacion(anotacion)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateAnotacion(id: Int, anotacion: AnotacionPersonal): AnotacionPersonal =
        api.updateAnotacion(id, anotacion)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteAnotacion(id: Int) {
        api.deleteAnotacion(id)
    }
}
