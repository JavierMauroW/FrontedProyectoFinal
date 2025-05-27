package com.example.appproyecto.apps

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


private val fondoGradiente = Brush.verticalGradient(
    colors = listOf(Color(0xFF181818), Color(0xFF22183c), Color(0xFF1a1033))
)
private val cardColor = Color(0xFF232232)
private val cardShadow = Color(0x45000000)
private val primaryColor = Color(0xFFB26DFF)
private val accentColor = Color(0xFF85E3FF)
private val secondaryColor = Color.White
private val dividerColor = Color(0x33FFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPantalla(navController: NavController, idUsuario: Int) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoGradiente),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(fondoGradiente),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            HeaderMenu()

            Spacer(Modifier.height(24.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                MenuCard(
                    title = "Anotaciones",
                    subtitle = "Tus notas personales",
                    icon = Icons.Default.FolderOpen,
                    accent = primaryColor,
                    onClick = { navController.navigate("anotaciones_personal/$idUsuario") }
                )
                Spacer(Modifier.height(16.dp))
                MenuCard(
                    title = "Notas rápidas",
                    subtitle = "Ideas y recordatorios",
                    icon = Icons.Default.Edit,
                    accent = accentColor,
                    onClick = { navController.navigate("notas_rapidas/$idUsuario") }
                )
                Spacer(Modifier.height(16.dp))
                MenuCard(
                    title = "Objetivos",
                    subtitle = "Metas y hábitos",
                    icon = Icons.Default.CheckCircle,
                    accent = Color(0xFF4CAF50),
                    onClick = { navController.navigate("objetivos/$idUsuario") }
                )
                Spacer(Modifier.height(16.dp))
                MenuCard(
                    title = "Progreso",
                    subtitle = "Estadísticas y logros",
                    icon = Icons.Default.TrendingUp,
                    accent = Color(0xFFEFCB68),
                    onClick = { navController.navigate("progreso_objetivos/$idUsuario") }
                )
                Spacer(Modifier.height(16.dp))
                MenuCard(
                    title = "Resumen",
                    subtitle = "Tu avance en un vistazo",
                    icon = Icons.Default.Assessment,
                    accent = Color(0xFFFF5E7E),
                    onClick = { navController.navigate("resumen_progreso/$idUsuario") }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Divider(
                color = dividerColor,
                thickness = 1.dp,
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 10.dp)
            )


            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = primaryColor
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp, brush = Brush.horizontalGradient(listOf(primaryColor, accentColor)))
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Cerrar Sesión",
                    tint = primaryColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Cerrar Sesión",
                    color = primaryColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun HeaderMenu() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 8.dp)
    ) {

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.15f))
                .border(width = 2.dp, color = primaryColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                tint = primaryColor,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(Modifier.height(12.dp))

        Text(
            "MindNotes",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = secondaryColor,
            letterSpacing = 1.5.sp
        )
        Text(
            "Tu espacio de productividad",
            fontSize = 15.sp,
            color = secondaryColor.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, label = "")
    val elevation by animateFloatAsState(if (pressed) 2f else 8f, label = "")
    val bgColor by animateColorAsState(if (pressed) cardColor.copy(alpha = 0.95f) else cardColor, label = "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .blur(if (pressed) 0.5.dp else 0.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                    },
                    onTap = { onClick() }
                )
            },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    title,
                    color = secondaryColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    subtitle,
                    color = secondaryColor.copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = accent.copy(alpha = 0.8f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}