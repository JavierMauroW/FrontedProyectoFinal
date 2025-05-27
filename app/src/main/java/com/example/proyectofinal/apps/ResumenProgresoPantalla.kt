package com.example.proyectofinal.apps

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.ViewModel.ResumenProgresoViewModel
import com.example.proyectofinal.model.ResumenProgreso

@Composable
fun ResumenProgresoPantalla(
    navController: NavController,
    viewModel: ResumenProgresoViewModel = viewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.loadResúmenes()
    }

    val resúmenes by viewModel.resúmenes.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Resumen del día",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        if (resúmenes.isNotEmpty()) {
            resúmenes.forEach { resumen ->
                Text(
                    text = "✔ Objetivos completados: ${resumen.objetivosCompletados}/${resumen.totalObjetivos}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "💬 Comentario del resumen: ${resumen.comentarioResumen}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        } else {
            Text(
                text = "No hay resúmenes disponibles.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }


        error?.let { errorMessage ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
