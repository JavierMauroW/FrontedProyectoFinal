// --- RegistroPantalla.kt ---
package com.example.proyectofinal.apps

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectofinal.ViewModel.RegistroViewModel

// Colores
private val fondoColor = Color(0xFFFDF6E3)
private val lineaColor = Color(0xFFB3CDE0)
private val primaryColor = Color(0xFFB26D00)
private val secondaryColor = Color(0xFF8C5600)

@Composable
fun RegistroPantalla(
    navController: NavController,
    viewModel: RegistroViewModel = viewModel()
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmaContrasena by remember { mutableStateOf("") }
    val mensajeError by viewModel.mensaje.collectAsState()
    val usuarioCreado by viewModel.usuarioCreado.collectAsState()
    val context = LocalContext.current

    // Auto-navegar cuando el usuario se crea
    LaunchedEffect(usuarioCreado) {
        usuarioCreado?.let { u ->
            Toast.makeText(context, "¡Bienvenido, ${u.correo}!", Toast.LENGTH_SHORT).show()
            viewModel.limpiar()
            navController.navigate("menu/${u.idUsuario}") {
                popUpTo("registro") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Crear Cuenta", fontSize = 28.sp, color = primaryColor)

            Spacer(Modifier.height(24.dp))

            // Correo
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo", color = secondaryColor) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = lineaColor,
                    focusedBorderColor = primaryColor,
                    cursorColor = primaryColor
                )
            )

            Spacer(Modifier.height(12.dp))

            // Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña", color = secondaryColor) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = lineaColor,
                    focusedBorderColor = primaryColor,
                    cursorColor = primaryColor
                )
            )

            Spacer(Modifier.height(12.dp))

            // Confirmar contraseña
            OutlinedTextField(
                value = confirmaContrasena,
                onValueChange = { confirmaContrasena = it },
                label = { Text("Confirmar contraseña", color = secondaryColor) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = lineaColor,
                    focusedBorderColor = primaryColor,
                    cursorColor = primaryColor
                )
            )

            // Aviso si no coinciden
            if (contrasena.isNotEmpty() && confirmaContrasena.isNotEmpty() && contrasena != confirmaContrasena) {
                Text(
                    "Las contraseñas no coinciden",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    when {
                        correo.isBlank() || contrasena.isBlank() || confirmaContrasena.isBlank() -> {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                        contrasena != confirmaContrasena -> {
                            Toast.makeText(context, "Las contraseñas deben coincidir", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            viewModel.registrarUsuario(correo, contrasena)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("Registrarse", color = Color.White)
            }

            // Error backend o de validación
            mensajeError?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(msg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("¿Ya tienes cuenta? Iniciar sesión", color = secondaryColor)
            }
        }
    }
}
