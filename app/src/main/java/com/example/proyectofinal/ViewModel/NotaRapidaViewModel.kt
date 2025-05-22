package com.example.proyectofinal.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Repository.NotaRapidaRepository
import com.example.proyectofinal.model.NotaRapida
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotaRapidaViewModel(
    private val repository: NotaRapidaRepository = NotaRapidaRepository()
) : ViewModel() {

    private val _notas = MutableStateFlow<List<NotaRapida>>(emptyList())
    val notas: StateFlow<List<NotaRapida>> = _notas

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Carga todas las notas rápidas */
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadNotas() {
        viewModelScope.launch {
            try {
                _notas.value = repository.getAllNotas()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error cargando notas: ${e.localizedMessage}"
            }
        }
    }

    /** Agrega una nueva nota rápida y recarga */
    @RequiresApi(Build.VERSION_CODES.O)
    fun addNota(nota: NotaRapida) {
        viewModelScope.launch {
            try {
                repository.createNota(nota)
                loadNotas()
            } catch (e: Exception) {
                _error.value = "Error creando nota: ${e.localizedMessage}"
            }
        }
    }

    /** Edita una nota rápida y recarga */
    @RequiresApi(Build.VERSION_CODES.O)
    fun editNota(id: NotaRapida, nota: NotaRapida) {
        viewModelScope.launch {
            try {
                repository.updateNota(id, nota)
                loadNotas()
            } catch (e: Exception) {
                _error.value = "Error actualizando nota: ${e.localizedMessage}"
            }
        }
    }

    /** Elimina una nota rápida y recarga */
    @RequiresApi(Build.VERSION_CODES.O)
    fun removeNota(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteNota(id)
                loadNotas()
            } catch (e: Exception) {
                _error.value = "Error eliminando nota: ${e.localizedMessage}"
            }
        }
    }
}
