// --- RegistroViewModel.kt ---
package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Repository.UsuarioRepository
import com.example.proyectofinal.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repo: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    // Estado de error o éxito (mensaje)
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    // El usuario recién creado (null hasta que se registre)
    private val _usuarioCreado = MutableStateFlow<Usuario?>(null)
    val usuarioCreado: StateFlow<Usuario?> = _usuarioCreado

    /** Llama al backend para crear un nuevo Usuario */
    fun registrarUsuario(correo: String, contrasena: String) {
        viewModelScope.launch {
            try {
                val nuevo = Usuario(
                    idUsuario = 0,
                    nombre = "",
                    correo = correo,
                    contrasena = contrasena
                )
                val creado = repo.createUsuario(nuevo)
                _usuarioCreado.value = creado
                _mensaje.value = null
            } catch (e: Exception) {
                _mensaje.value = e.localizedMessage ?: "Error desconocido"
            }
        }
    }

    /** Limpia el estado una vez usado en la UI */
    fun limpiar() {
        _mensaje.value = null
        _usuarioCreado.value = null
    }
}
