package com.example.proyectofinal.Repository




import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.interfaces.RetrofitClient
import com.example.proyectofinal.model.Usuario

class LoginRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    private val api = RetrofitClient.apiService


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun login(correo: String, contrasena: String): Usuario =
        api.login(Usuario(
            idUsuario = 0,
            nombre    = "",
            correo    = correo,
            contrasena= contrasena
        ))
}
