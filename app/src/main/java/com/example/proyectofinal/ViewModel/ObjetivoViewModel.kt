package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Repository.ObjetivoRepository
import com.example.proyectofinal.model.Objetivo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ObjetivoViewModel(
    private val repository: ObjetivoRepository = ObjetivoRepository()
) : ViewModel() {

    private val _objetivos = MutableStateFlow<List<Objetivo>>(emptyList())
    val objetivos: StateFlow<List<Objetivo>> = _objetivos

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Carga todos los objetivos */
    fun loadObjetivos() {
        viewModelScope.launch {
            try {
                _objetivos.value = repository.getAllObjetivos()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error cargando objetivos: ${e.localizedMessage}"
            }
        }
    }

    /** Agrega un nuevo objetivo y recarga */
    fun addObjetivo(objetivo: Objetivo) {
        viewModelScope.launch {
            try {
                repository.createObjetivo(objetivo)
                loadObjetivos()
            } catch (e: Exception) {
                _error.value = "Error creando objetivo: ${e.localizedMessage}"
            }
        }
    }

    /** Edita un objetivo y recarga */
    fun editObjetivo(id: Int, objetivo: Objetivo) {
        viewModelScope.launch {
            try {
                repository.updateObjetivo(id, objetivo)
                loadObjetivos()
            } catch (e: Exception) {
                _error.value = "Error actualizando objetivo: ${e.localizedMessage}"
            }
        }
    }

    /** Elimina un objetivo y recarga */
    fun removeObjetivo(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteObjetivo(id)
                loadObjetivos()
            } catch (e: Exception) {
                _error.value = "Error eliminando objetivo: ${e.localizedMessage}"
            }
        }
    }
}
