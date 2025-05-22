package com.example.appproyecto.apps

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MenuPantalla(navController: NavController, idUsuario: Int) {
    // Colores estilo cuaderno
    val fondoColor = Color(0xFFFDF6E3) // Color de fondo
    val lineaColor = Color(0xFFB3CDE0) // Color de línea
    val primaryColor = Color(0xFFB26D00) // Color primario
    val secondaryColor = Color(0xFF8C5600) // Color secundario

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bienvenido",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                textAlign = TextAlign.Center
            )

            Divider(
                color = lineaColor,
                thickness = 2.dp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.6f)
            )

            // Botones de menú con animaciones e iconos
            BotonMenu(texto = "Anotaciones", icono = Icons.Default.Note, colorFondo = primaryColor) {
                navController.navigate("anotaciones_personal/$idUsuario")
            }
            BotonMenu(texto = "Notas rápidas", icono = Icons.Default.Edit, colorFondo = primaryColor) {
                navController.navigate("notas_rapidas/$idUsuario")
            }
            BotonMenu(texto = "Objetivos", icono = Icons.Default.CheckCircle, colorFondo = primaryColor) {
                navController.navigate("objetivos/$idUsuario")
            }
            BotonMenu(texto = "Progreso de objetivos", icono = Icons.Default.TrendingUp, colorFondo = primaryColor) {
                navController.navigate("progreso_objetivos/$idUsuario")
            }
            BotonMenu(texto = "Resumen del día", icono = Icons.Default.Assessment, colorFondo = primaryColor) {
                navController.navigate("resumen_progreso/$idUsuario")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de cerrar sesión
            BotonMenu(texto = "Cerrar Sesión", icono = Icons.Default.ExitToApp, colorFondo = secondaryColor) {
                navController.navigate("login") // Navegar a la pantalla de login
            }
        }
    }
}

@Composable
fun BotonMenu(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorFondo: Color,
    onClick: () -> Unit
) {
    // Animación de escala
    var scale by remember { mutableStateOf(1f) }
    val infiniteTransition = rememberInfiniteTransition()
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(50.dp)
            .scale(animatedScale), // Aplicar la animación de escala
        colors = ButtonDefaults.buttonColors(containerColor = colorFondo),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
        Icon(icono, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
