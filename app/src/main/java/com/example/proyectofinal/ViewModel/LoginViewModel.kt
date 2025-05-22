package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Repository.LoginRepository
import com.example.proyectofinal.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val repo: LoginRepository = LoginRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<Usuario?>(null)
    val user: StateFlow<Usuario?> = _user.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Inicia sesión con correo y contraseña */
    fun login(correo: String, contrasena: String) {
        viewModelScope.launch {
            try {
                val u = repo.login(correo, contrasena)
                _user.value = u
                _error.value = null
            } catch (e: HttpException) {
                _user.value = null  // asegúrate de limpiar usuario previo
                _error.value = when (e.code()) {
                    401 -> "Contraseña incorrecta"
                    404 -> "Usuario no encontrado"
                    else -> "Error de servidor: ${e.code()}"
                }
            } catch (e: Exception) {
                _user.value = null
                _error.value = e.localizedMessage ?: "Error desconocido"
            }
        }
    }
}



