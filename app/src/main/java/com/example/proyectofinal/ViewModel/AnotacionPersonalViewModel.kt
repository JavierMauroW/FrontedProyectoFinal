package com.example.proyectofinal.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.Repository.AnotacionPersonalRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.model.AnotacionPersonal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class AnotacionPersonalViewModel(
    private val repository: AnotacionPersonalRepository = AnotacionPersonalRepository()
) : ViewModel() {

    private val _anotaciones = MutableStateFlow<List<AnotacionPersonal>>(emptyList())
    val anotaciones: StateFlow<List<AnotacionPersonal>> = _anotaciones

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadAnotaciones()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadAnotaciones() {
        viewModelScope.launch {
            try {
                _anotaciones.value = repository.getAllAnotaciones()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error cargando anotaciones: ${e.localizedMessage}"
            }
        }
    }

    fun addAnotacion(anotacion: AnotacionPersonal) {
        viewModelScope.launch {
            try {
                repository.createAnotacion(anotacion)
                loadAnotaciones()
            } catch (e: Exception) {
                _error.value = "Error creando anotación: ${e.localizedMessage}"
            }
        }
    }

    fun removeAnotacion(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteAnotacion(id)
                loadAnotaciones()
            } catch (e: Exception) {
                _error.value = "Error eliminando anotación: ${e.localizedMessage}"
            }
        }
    }
}
