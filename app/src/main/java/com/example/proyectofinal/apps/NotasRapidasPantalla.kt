package com.example.proyectofinal.apps

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.ViewModel.NotaRapidaViewModel
import com.example.proyectofinal.model.NotaRapida
import com.example.proyectofinal.model.Usuario
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasRapidasPantalla(
    navController: androidx.navigation.NavController,
    idUsuario: Int,
    viewModel: NotaRapidaViewModel = viewModel()
) {
    val context = LocalContext.current

    // Estado para edición de nota
    var notaOriginalEdicion by remember { mutableStateOf<NotaRapida?>(null) }
    var textoNota by remember { mutableStateOf("") }
    var esRecordatorio by remember { mutableStateOf(false) }
    var recordatorioMinutos by remember { mutableStateOf<String>("") } // Minutos para recordatorio
    var mensaje by remember { mutableStateOf<String?>(null) }

    val notas by viewModel.notas.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* … */ }
    )

    LaunchedEffect(Unit) {
        viewModel.loadNotas()
        crearCanalDeNotificacion(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8E9))
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Notas Rápidas",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF2E7D32)
            )
        )

        if (esRecordatorio) {
            TextField(
                value = recordatorioMinutos,
                onValueChange = {
                    if (it.all { c -> c.isDigit() }) recordatorioMinutos = it
                },
                placeholder = { Text("Minutos para recordatorio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.small,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFC8E6C9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF1B5E20)
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = esRecordatorio,
                onCheckedChange = { esRecordatorio = it; if (!it) recordatorioMinutos = "" },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF388E3C))
            )
            Text(
                "Marcar como recordatorio",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.width(12.dp))
            if (esRecordatorio) {
                TextButton(
                    onClick = {
                        enviarNotificacion(context, textoNota)
                        recordatorioMinutos = ""
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1B5E20))
                ) {
                    Text("Recordatorio inmediato")
                }
            }
        }

        TextField(
            value = textoNota,
            onValueChange = { textoNota = it; mensaje = null },
            placeholder = { Text("Escribe tu nota aquí") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFC8E6C9),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF1B5E20)
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
        )

        Button(
            onClick = {
                if (textoNota.isNotBlank()) {
                    val notaEditada = NotaRapida(
                        idNota = notaOriginalEdicion?.idNota ?: 0,
                        contenido = textoNota,
                        fechaCreacion = LocalDate.now(),
                        esRecordatorio = esRecordatorio,
                        usuario = Usuario(idUsuario, "", "", "")
                    )
                    if (notaOriginalEdicion != null) {
                        viewModel.editNota(notaOriginalEdicion!!, notaEditada)
                    } else {
                        viewModel.addNota(notaEditada)
                    }

                    if (esRecordatorio) {
                        val minutos = recordatorioMinutos.toLongOrNull()
                        if (minutos != null && minutos > 0) {
                            programarNotificacionConDelay(context, textoNota, minutos)
                        } else {
                            enviarNotificacion(context, textoNota)
                        }
                    }

                    textoNota = ""
                    esRecordatorio = false
                    recordatorioMinutos = ""
                    notaOriginalEdicion = null
                    mensaje = "Nota guardada"
                } else {
                    mensaje = "La nota no puede estar vacía"
                }
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                if (notaOriginalEdicion != null) "Actualizar" else "Guardar",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        mensaje?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notas) { nota ->
                val fondo = if (nota.esRecordatorio) Color(0xFFFFF9C4) else Color(0xFFDCEDC8)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = fondo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = nota.contenido,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Fecha: ${nota.fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                            fontSize = 13.sp,
                            color = Color(0xFF4E342E)
                        )
                        if (nota.esRecordatorio) {
                            Text(
                                "🔔 Recordatorio",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD84315)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                viewModel.removeNota(nota.idNota)
                                if (notaOriginalEdicion?.idNota == nota.idNota) {
                                    textoNota = ""
                                    esRecordatorio = false
                                    recordatorioMinutos = ""
                                    notaOriginalEdicion = null
                                }
                            }) {
                                Text("Eliminar", color = Color.Red)
                            }
                            Spacer(Modifier.width(12.dp))
                            TextButton(onClick = {
                                textoNota = nota.contenido
                                esRecordatorio = nota.esRecordatorio
                                recordatorioMinutos = ""
                                notaOriginalEdicion = nota
                            }) {
                                Text("Editar", color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                navController.navigate("menu/$idUsuario") {
                    popUpTo("menu/{$idUsuario}") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Volver al Menú Principal", color = Color.White, fontSize = 18.sp)
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun programarNotificacionConDelay(context: Context, contenido: String, minutos: Long) {
    GlobalScope.launch {
        delay(TimeUnit.MINUTES.toMillis(minutos))
        enviarNotificacion(context, contenido)
    }
}

fun crearCanalDeNotificacion(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val canal = NotificationChannel(
            "canal_rapidas",
            "Notas Rápidas",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal para notas como recordatorio"
        }
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(canal)
    }
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun enviarNotificacion(context: Context, contenido: String) {
    val builder = NotificationCompat.Builder(context, "canal_rapidas")
        .setSmallIcon(R.drawable.ic_popup_reminder)
        .setContentTitle("🔔 Recordatorio Nota")
        .setContentText(contenido)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    NotificationManagerCompat.from(context)
        .notify(System.currentTimeMillis().toInt(), builder.build())
}