package com.example.proyectofinal.Repository




import com.example.proyectofinal.interfaces.RetrofitClient
import com.example.proyectofinal.model.Usuario

class LoginRepository {
    private val api = RetrofitClient.apiService

    /**
     * Llama al endpoint POST /api/login enviando un Usuario con correo y contraseña.
     * Devuelve el Usuario completo (o lanza excepción si falla).
     */
    suspend fun login(correo: String, contrasena: String): Usuario =
        api.login(Usuario(
            idUsuario = 0,    // sólo importa correo y contraseña
            nombre    = "",
            correo    = correo,
            contrasena= contrasena
        ))
}
