package com.example.proyectofinal.apps

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.ViewModel.ProgresoObjetivoViewModel
import com.example.proyectofinal.model.ProgresoObjetivo

@Composable
fun ProgresoObjetivosPantalla(navController: NavController, idUsuario: Int, viewModel: ProgresoObjetivoViewModel = viewModel()) {
    // Cargar los progresos al inicio
    LaunchedEffect(Unit) {
        viewModel.loadProgresos()
    }

    val progresos by viewModel.progresos.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Progreso de objetivos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        progresos.forEach { progreso ->
            Text(text = progreso.progresoDiario)

            LinearProgressIndicator(
                progress = 0.5f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        error?.let { errorMessage ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
