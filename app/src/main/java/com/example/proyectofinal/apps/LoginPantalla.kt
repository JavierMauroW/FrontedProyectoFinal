package com.example.proyectofinal.apps

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectofinal.ViewModel.LoginViewModel

@Composable
fun LoginPantalla(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val error by viewModel.error.collectAsState()
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current

    // Navega al menú si login fue exitoso
    LaunchedEffect(user) {
        user?.let { u ->
            navController.navigate("menu/${u.idUsuario}") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // Colores estilo “diario/cuaderno”
    val fondoColor = Color(0xFFFDF6E3)       // color papel
    val lineColor = Color(0xFFB3CDE0)        // líneas horizontales
    val marginColor = Color(0xFFDD6E2C)      // margen vertical
    val primaryColor = Color(0xFFB26D00)     // botones, foco
    val secondaryColor = Color(0xFF8C5600)   // textos

    // Espaciado entre líneas de cuaderno
    val lineSpacing = 32.dp

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
            .drawBehind {
                // Dibujar líneas horizontales a modo de cuaderno:
                val spacingPx = lineSpacing.toPx()
                var y = spacingPx
                while (y < size.height) {
                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                        strokeWidth = 1.dp.toPx(),
                        cap = StrokeCap.Square
                    )
                    y += spacingPx
                }
                // Dibujar margen vertical:
                val marginX = 48.dp.toPx()
                drawLine(
                    color = marginColor,
                    start = androidx.compose.ui.geometry.Offset(marginX, 0f),
                    end = androidx.compose.ui.geometry.Offset(marginX, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            },
        color = fondoColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "MindNotes",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Iniciar Sesión",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = secondaryColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = lineColor,
                            cursorColor = primaryColor
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = lineColor,
                            cursorColor = primaryColor
                        )
                    )

                    error?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            it,
                            color = Color.Red,
                            style = TextStyle(fontSize = 14.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (correo.isBlank() || password.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Por favor, completa todos los campos.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModel.login(correo, password)
                            }
                        },


                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Text("Entrar", fontSize = 16.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { navController.navigate("registro") }) {
                        Text("¿No tienes cuenta? Regístrate",
                            color = secondaryColor,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
