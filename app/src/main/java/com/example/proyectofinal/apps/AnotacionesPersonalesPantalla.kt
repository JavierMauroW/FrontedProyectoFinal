package com.example.proyectofinal.apps

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectofinal.ViewModel.AnotacionPersonalViewModel
import com.example.proyectofinal.model.AnotacionPersonal
import com.example.proyectofinal.model.Usuario
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnotacionesPersonalesPantalla(
    navController: NavController,
    idUsuario: Int,
    viewModel: AnotacionPersonalViewModel = viewModel()
) {
    val context = LocalContext.current
    val anotaciones by viewModel.anotaciones.collectAsState()
    val error by viewModel.error.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<AnotacionPersonal?>(null) }

    val fondoColor = Color.Black
    val textColor = Color.White
    val cardColor = Color(0xFF1C1C1C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor)
            .padding(16.dp)
    ) {
        Text(
            text = "Mis Anotaciones",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(anotaciones) { anot ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .combinedClickable(
                            onClick = { navController.navigate("editar_anotacion/${anot.idAnotacion}") },
                            onLongClick = {
                                toDelete = anot
                                showDialog = true
                            }
                        ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = anot.titulo,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = anot.contenido,
                            fontSize = 16.sp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = anot.fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Diálogo de confirmación para eliminar anotación
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Eliminar anotación", color = textColor) },
                text = { Text("¿Estás seguro de que deseas eliminar esta anotación?", color = Color.Gray) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.removeAnotacion(toDelete!!.idAnotacion)
                            showDialog = false
                            Toast.makeText(context, "Anotación eliminada", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Botón para volver al menú
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("menu/$idUsuario") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513))
        ) {
            Text("Volver al Menú Principal", color = Color.White, fontSize = 16.sp)
        }
    }
}
