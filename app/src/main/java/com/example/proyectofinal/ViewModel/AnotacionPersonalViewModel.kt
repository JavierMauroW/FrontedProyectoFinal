package com.example.proyectofinal.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Repository.AnotacionPersonalRepository
import com.example.proyectofinal.model.AnotacionPersonal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class AnotacionPersonalViewModel(
    private val repository: AnotacionPersonalRepository = AnotacionPersonalRepository()
) : ViewModel() {

    private val _anotaciones = MutableStateFlow<List<AnotacionPersonal>>(emptyList())
    val anotaciones: StateFlow<List<AnotacionPersonal>> = _anotaciones.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAnotaciones()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadAnotaciones() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _anotaciones.value = repository.getAllAnotaciones()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error cargando anotaciones: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addAnotacion(anotacion: AnotacionPersonal) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createAnotacion(anotacion)
                loadAnotaciones() // recarga ya gestiona isLoading
            } catch (e: Exception) {
                _error.value = "Error creando anotación: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeAnotacion(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteAnotacion(id)
                loadAnotaciones()
            } catch (e: Exception) {
                _error.value = "Error eliminando anotación: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateAnotacion(id: Int, anotacion: AnotacionPersonal) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateAnotacion(id, anotacion)
                loadAnotaciones()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error actualizando anotación: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun getAnotacionById(id: Int): AnotacionPersonal? {
        return _anotaciones.value.find { it.idAnotacion == id }
    }

    fun editAnotacion(anotacion: AnotacionPersonal) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateAnotacion(anotacion.idAnotacion, anotacion)
                loadAnotaciones()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error actualizando anotación: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}