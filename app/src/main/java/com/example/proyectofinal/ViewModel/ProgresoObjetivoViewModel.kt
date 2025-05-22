package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Repository.ProgresoObjetivoRepository
import com.example.proyectofinal.model.ProgresoObjetivo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProgresoObjetivoViewModel(
    private val repository: ProgresoObjetivoRepository = ProgresoObjetivoRepository()
) : ViewModel() {

    private val _progresos = MutableStateFlow<List<ProgresoObjetivo>>(emptyList())
    val progresos: StateFlow<List<ProgresoObjetivo>> = _progresos

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Carga todos los progresos */
    fun loadProgresos() {
        viewModelScope.launch {
            try {
                _progresos.value = repository.getAllProgresos()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error cargando progresos: ${e.localizedMessage}"
            }
        }
    }

    /** Agrega un progreso y recarga */
    fun addProgreso(progreso: ProgresoObjetivo) {
        viewModelScope.launch {
            try {
                repository.createProgreso(progreso)
                loadProgresos()
            } catch (e: Exception) {
                _error.value = "Error creando progreso: ${e.localizedMessage}"
            }
        }
    }

    /** Edita un progreso y recarga */
    fun editProgreso(id: Int, progreso: ProgresoObjetivo) {
        viewModelScope.launch {
            try {
                repository.updateProgreso(id, progreso)
                loadProgresos()
            } catch (e: Exception) {
                _error.value = "Error actualizando progreso: ${e.localizedMessage}"
            }
        }
    }

    /** Elimina un progreso y recarga */
    fun removeProgreso(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteProgreso(id)
                loadProgresos()
            } catch (e: Exception) {
                _error.value = "Error eliminando progreso: ${e.localizedMessage}"
            }
        }
    }
}
