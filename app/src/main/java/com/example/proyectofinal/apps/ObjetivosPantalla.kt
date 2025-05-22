package com.example.proyectofinal.apps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.ViewModel.ObjetivoViewModel
import com.example.proyectofinal.model.Objetivo
import com.example.proyectofinal.model.Usuario
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ObjetivosPantalla(navController: NavController, idUsuario: Int, viewModel: ObjetivoViewModel = viewModel()) {
    var objetivo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Nuevo objetivo",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = objetivo,
            onValueChange = { objetivo = it },
            label = { Text("Escribe tu objetivo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (objetivo.isNotBlank()) {
                    val nuevoObjetivo = Objetivo(
                        idObjetivo = 0,
                        titulo = objetivo,  // Recuerda que en tu modelo también hay "titulo", no solo descripcion
                        descripcion = "",   // O lo que corresponda, o agregar otro campo para ingresar descripción
                        fechaInicio = LocalDate.now(),
                        fechaFin = LocalDate.now().plusDays(7), // o fecha que corresponda
                        completado = false,
                        usuario = Usuario(
                            idUsuario = idUsuario,
                            nombre = "", // Puedes dejar vacío si no tienes esos datos en ese momento
                            correo = "",
                            contrasena = ""
                        )
                    )
                    viewModel.addObjetivo(nuevoObjetivo)
                    mensaje = "Objetivo guardado para el usuario $idUsuario"
                    objetivo = ""
                } else {
                    mensaje = "El objetivo no puede estar vacío"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        if (mensaje.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = mensaje, color = MaterialTheme.colorScheme.primary)
        }
    }
}
