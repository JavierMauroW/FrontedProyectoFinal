package com.example.proyectofinal.Repository



import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.interfaces.RetrofitClient
import com.example.proyectofinal.model.ResumenProgreso

class ResumenProgresoRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    private val api = RetrofitClient.apiService

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllResumenes(): List<ResumenProgreso> =
        api.getAllResumenes()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createResumen(resumen: ResumenProgreso): ResumenProgreso =
        api.createResumen(resumen)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateResumen(id: Int, resumen: ResumenProgreso): ResumenProgreso =
        api.updateResumen(id, resumen)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteResumen(id: Int) {
        api.deleteResumen(id)
    }
}
