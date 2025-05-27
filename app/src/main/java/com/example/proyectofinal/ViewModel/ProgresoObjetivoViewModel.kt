package com.example.proyectofinal.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Repository.ProgresoObjetivoRepository
import com.example.proyectofinal.model.ProgresoObjetivo
import com.example.proyectofinal.model.Objetivo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProgresoObjetivoViewModel(
    private val repository: ProgresoObjetivoRepository = ProgresoObjetivoRepository()
) : ViewModel() {

    private val _progresos = MutableStateFlow<List<ProgresoObjetivo>>(emptyList())
    val progresos: StateFlow<List<ProgresoObjetivo>> = _progresos

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    @RequiresApi(Build.VERSION_CODES.O)
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


    @RequiresApi(Build.VERSION_CODES.O)
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


    @RequiresApi(Build.VERSION_CODES.O)
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


    @RequiresApi(Build.VERSION_CODES.O)
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarProgresoManual(objetivo: Objetivo, comentario: String = "", progresoDiario: String = "") {
        viewModelScope.launch {
            try {
                val nuevoProgreso = ProgresoObjetivo(
                    idProgreso = 0, // o null si tu modelo lo permite
                    fecha = LocalDate.now(),
                    progresoDiario = progresoDiario,
                    comentario = comentario,
                    objetivo = objetivo // Se pasa el objeto completo
                )
                repository.createProgreso(nuevoProgreso)
                loadProgresos()
            } catch (e: Exception) {
                _error.value = "Error agregando progreso manual: ${e.localizedMessage}"
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun retrocederProgresoManual(objetivo: Objetivo) {
        viewModelScope.launch {
            try {

                val progresosObjetivo = repository.getAllProgresos().filter { it.objetivo.idObjetivo == objetivo.idObjetivo }
                val ultimo = progresosObjetivo.maxByOrNull { it.fecha }
                if (ultimo != null) {
                    repository.deleteProgreso(ultimo.idProgreso)
                    loadProgresos()
                }
            } catch (e: Exception) {
                _error.value = "Error retrocediendo progreso manual: ${e.localizedMessage}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}