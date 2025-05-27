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


    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje


    private val _usuarioCreado = MutableStateFlow<Usuario?>(null)
    val usuarioCreado: StateFlow<Usuario?> = _usuarioCreado


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


    fun limpiar() {
        _mensaje.value = null
        _usuarioCreado.value = null
    }
}
