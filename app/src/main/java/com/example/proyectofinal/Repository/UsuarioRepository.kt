package com.example.proyectofinal.Repository



import com.example.proyectofinal.interfaces.RetrofitClient
import com.example.proyectofinal.model.Usuario

class UsuarioRepository {
    private val api = RetrofitClient.apiService

    suspend fun getAllUsuarios(): List<Usuario> =
        api.getAllUsuarios()

    suspend fun createUsuario(usuario: Usuario): Usuario =
        api.createUsuario(usuario)

    suspend fun updateUsuario(id: Int, usuario: Usuario): Usuario =
        api.updateUsuario(id, usuario)

    suspend fun deleteUsuario(id: Int) {
        api.deleteUsuario(id)
    }

    suspend fun login(usuario: Usuario): Usuario =
        api.login(usuario)
}
