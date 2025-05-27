package com.example.proyectofinal.apps

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectofinal.ViewModel.LoginViewModel
import androidx.compose.ui.input.pointer.pointerInput

// --- Paleta de colores consistente con el menú ---
private val fondoGradiente = Brush.verticalGradient(
    colors = listOf(Color(0xFF181818), Color(0xFF22183c), Color(0xFF1a1033))
)
private val cardColor = Color(0xFF232232)
private val primaryColor = Color(0xFFB26DFF)
private val accentColor = Color(0xFF85E3FF)
private val secondaryColor = Color.White
private val borderColor = Color(0xFF353535)

@OptIn(ExperimentalMaterial3Api::class)
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

    LaunchedEffect(user) {
        user?.let { u ->
            navController.navigate("menu/${u.idUsuario}") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoGradiente),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(fondoGradiente)
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.17f))
                        .border(width = 2.dp, color = primaryColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Login",
                        tint = primaryColor,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "MindNotes",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = secondaryColor,
                    letterSpacing = 1.2.sp
                )
                Text(
                    "Tu espacio de productividad",
                    fontSize = 15.sp,
                    color = secondaryColor.copy(alpha = 0.55f),
                    fontWeight = FontWeight.Medium
                )
            }


            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .blur(0.5.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 32.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Inicia sesión",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = secondaryColor
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.MailOutline,
                                contentDescription = null,
                                tint = primaryColor
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = cardColor,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = borderColor,
                            cursorColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            unfocusedLabelColor = Color(0xFFAAAAAA)
                        ),
                        textStyle = TextStyle(color = secondaryColor)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = primaryColor
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = cardColor,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = borderColor,
                            cursorColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            unfocusedLabelColor = Color(0xFFAAAAAA)
                        ),
                        textStyle = TextStyle(color = secondaryColor)
                    )


                    error?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            it,
                            color = Color(0xFFFF5E7E),
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))


                    var pressed by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, label = "")
                    val btnBgColor by animateColorAsState(
                        if (pressed) primaryColor.copy(alpha = 0.85f) else primaryColor,
                        label = ""
                    )

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
                            .height(50.dp)
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        pressed = true
                                        try {
                                            awaitRelease()
                                        } finally {
                                            pressed = false
                                        }
                                    }
                                )
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = btnBgColor)
                    ) {
                        Text("Entrar", fontSize = 16.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TextButton(onClick = { navController.navigate("registro") }) {
                        Text(
                            "¿No tienes cuenta? Regístrate",
                            color = primaryColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}