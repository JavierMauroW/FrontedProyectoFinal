package com.example.proyectofinal.ViewModel

import com.example.proyectofinal.Repository.UsuarioRepository



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.model.Usuario

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Carga todos los usuarios */
    fun loadUsuarios() {
        viewModelScope.launch {
            try {
                _usuarios.value = repository.getAllUsuarios()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error cargando usuarios: ${e.localizedMessage}"
            }
        }
    }

    /** Agrega un nuevo usuario y recarga */
    fun addUsuario(usuario: Usuario) {
        viewModelScope.launch {
            try {
                repository.createUsuario(usuario)
                loadUsuarios()
            } catch (e: Exception) {
                _error.value = "Error creando usuario: ${e.localizedMessage}"
            }
        }
    }

    /** Edita un usuario y recarga */
    fun editUsuario(id: Int, usuario: Usuario) {
        viewModelScope.launch {
            try {
                repository.updateUsuario(id, usuario)
                loadUsuarios()
            } catch (e: Exception) {
                _error.value = "Error actualizando usuario: ${e.localizedMessage}"
            }
        }
    }

    /** Elimina un usuario y recarga */
    fun removeUsuario(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteUsuario(id)
                loadUsuarios()
            } catch (e: Exception) {
                _error.value = "Error eliminando usuario: ${e.localizedMessage}"
            }
        }
    }
}
