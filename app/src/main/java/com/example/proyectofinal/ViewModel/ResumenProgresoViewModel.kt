package com.example.proyectofinal.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
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


    @RequiresApi(Build.VERSION_CODES.O)
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
    @RequiresApi(Build.VERSION_CODES.O)
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


    @RequiresApi(Build.VERSION_CODES.O)
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


    @RequiresApi(Build.VERSION_CODES.O)
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
