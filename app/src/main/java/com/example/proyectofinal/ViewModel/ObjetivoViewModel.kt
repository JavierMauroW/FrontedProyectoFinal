package com.example.proyectofinal.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Repository.ObjetivoRepository
import com.example.proyectofinal.model.Objetivo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class ObjetivoViewModel(
    private val repository: ObjetivoRepository = ObjetivoRepository()
) : ViewModel() {

    private val _objetivos = MutableStateFlow<List<Objetivo>>(emptyList())
    val objetivos: StateFlow<List<Objetivo>> = _objetivos.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadObjetivos()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun loadObjetivos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _objetivos.value = repository.getAllObjetivos()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error cargando objetivos: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun addObjetivo(objetivo: Objetivo) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createObjetivo(objetivo)
                loadObjetivos()
            } catch (e: Exception) {
                _error.value = "Error creando objetivo: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun editObjetivo(id: Int, objetivo: Objetivo) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateObjetivo(id, objetivo)
                loadObjetivos()
            } catch (e: Exception) {
                _error.value = "Error actualizando objetivo: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun removeObjetivo(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteObjetivo(id)
                loadObjetivos()
            } catch (e: Exception) {
                _error.value = "Error eliminando objetivo: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun marcarObjetivoComoCompletado(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.marcarObjetivoComoCompletado(id)
                loadObjetivos()
            } catch (e: Exception) {
                _error.value = "Error marcando como completado: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}