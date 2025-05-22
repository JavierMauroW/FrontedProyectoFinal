package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Repository.ResumenProgresoRepository
import com.example.proyectofinal.model.ResumenProgreso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResumenProgresoViewModel(
    private val repository: ResumenProgresoRepository = ResumenProgresoRepository()
) : ViewModel() {

    private val _resúmenes = MutableStateFlow<List<ResumenProgreso>>(emptyList())
    val resúmenes: StateFlow<List<ResumenProgreso>> = _resúmenes

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Carga todos los resúmenes de progreso */
    fun loadResúmenes() {
        viewModelScope.launch {
            try {
                _resúmenes.value = repository.getAllResumenes()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error cargando resúmenes: ${e.localizedMessage}"
            }
        }
    }

    /** Agrega un nuevo resumen y recarga */
    fun addResumen(resumen: ResumenProgreso) {
        viewModelScope.launch {
            try {
                repository.createResumen(resumen)
                loadResúmenes()
            } catch (e: Exception) {
                _error.value = "Error creando resumen: ${e.localizedMessage}"
            }
        }
    }

    /** Edita un resumen y recarga */
    fun editResumen(id: Int, resumen: ResumenProgreso) {
        viewModelScope.launch {
            try {
                repository.updateResumen(id, resumen)
                loadResúmenes()
            } catch (e: Exception) {
                _error.value = "Error actualizando resumen: ${e.localizedMessage}"
            }
        }
    }

    /** Elimina un resumen y recarga */
    fun removeResumen(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteResumen(id)
                loadResúmenes()
            } catch (e: Exception) {
                _error.value = "Error eliminando resumen: ${e.localizedMessage}"
            }
        }
    }
}
