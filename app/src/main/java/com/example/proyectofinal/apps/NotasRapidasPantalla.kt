package com.example.proyectofinal.apps

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.ViewModel.NotaRapidaViewModel
import com.example.proyectofinal.model.NotaRapida
import com.example.proyectofinal.model.Usuario
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun NotasRapidasPantalla(
    navController: androidx.navigation.NavController,
    idUsuario: Int,
    viewModel: NotaRapidaViewModel = viewModel()
) {
    // Paleta profesional y consistente
    val fondoGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF181818), Color(0xFF22183c), Color(0xFF1a1033))
    )
    val cardColor = Color(0xFF232232)
    val accent = Color(0xFFB26DFF)
    val onAccent = Color.White
    val borderColor = Color(0xFF353535)
    val secondaryHighlight = Color(0xFF85E3FF)

    val context = LocalContext.current
    var notaOriginalEdicion by remember { mutableStateOf<NotaRapida?>(null) }
    var textoNota by remember { mutableStateOf("") }
    var esRecordatorio by remember { mutableStateOf(false) }
    var recordatorioMinutos by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var notaToDelete by remember { mutableStateOf<NotaRapida?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showCopied by remember { mutableStateOf(false) }
    val notas by viewModel.notas.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
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


    val notasFiltradas = if (searchQuery.isBlank()) notas else notas.filter { it.contenido.contains(searchQuery, true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(fondoGradient)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = {
                            navController.navigate("menu/$idUsuario") {

                                popUpTo("menu/$idUsuario") { inclusive = true }
                                launchSingleTop = true // No dupliques si ya está ahí
                                restoreState = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = accent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Notas Rápidas",
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.width(46.dp))
                }

                Spacer(Modifier.height(6.dp))


                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar nota...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = accent) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0xFF322C45),
                        focusedBorderColor = accent,
                        unfocusedBorderColor = borderColor,
                        cursorColor = accent,
                        focusedLabelColor = accent,
                        unfocusedLabelColor = Color(0xFFAAAAAA)
                    ),
                    textStyle = TextStyle(color = onAccent)
                )

                Spacer(Modifier.height(10.dp))


                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                shadowElevation = 12f
                                shape = RoundedCornerShape(20.dp)
                                clip = true
                            }
                            .blur(if (notaOriginalEdicion != null) 0.5.dp else 0.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(Modifier.padding(18.dp)) {
                            Text(
                                if (notaOriginalEdicion != null) "Editar nota" else "Nueva nota",
                                color = accent,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(10.dp))

                            OutlinedTextField(
                                value = textoNota,
                                onValueChange = { textoNota = it; mensaje = null },
                                placeholder = { Text("Escribe aquí...", color = Color.Gray) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 80.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    containerColor = Color(0xFF322C45),
                                    focusedBorderColor = accent,
                                    unfocusedBorderColor = borderColor,
                                    cursorColor = accent,
                                    focusedLabelColor = accent,
                                    unfocusedLabelColor = Color(0xFFAAAAAA)
                                ),
                                textStyle = TextStyle(color = onAccent),
                                shape = RoundedCornerShape(12.dp),
                                maxLines = 6
                            )

                            Spacer(Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = esRecordatorio,
                                    onCheckedChange = {
                                        esRecordatorio = it
                                        if (!it) recordatorioMinutos = ""
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = accent,
                                        uncheckedColor = Color.Gray
                                    )
                                )
                                Text(
                                    "Recordatorio",
                                    color = onAccent,
                                    modifier = Modifier.weight(1f)
                                )
                                if (notaOriginalEdicion != null) {
                                    IconButton(
                                        onClick = {

                                            textoNota = ""
                                            esRecordatorio = false
                                            recordatorioMinutos = ""
                                            notaOriginalEdicion = null
                                            mensaje = null
                                        },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(Color(0x30FFFFFF), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Cancelar edición",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }

                            AnimatedVisibility(visible = esRecordatorio) {
                                OutlinedTextField(
                                    value = recordatorioMinutos,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) recordatorioMinutos = it },
                                    placeholder = { Text("Minutos para recordatorio", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        containerColor = Color(0xFF322C45),
                                        focusedBorderColor = accent,
                                        unfocusedBorderColor = borderColor,
                                        cursorColor = accent,
                                        focusedLabelColor = accent,
                                        unfocusedLabelColor = Color(0xFFAAAAAA)
                                    ),
                                    textStyle = TextStyle(color = onAccent),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }

                            Spacer(Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (textoNota.isNotBlank()) {
                                        val notaNueva = NotaRapida(
                                            idNota = notaOriginalEdicion?.idNota ?: 0,
                                            contenido = textoNota,
                                            fechaCreacion = LocalDate.now(),
                                            esRecordatorio = esRecordatorio,
                                            usuario = Usuario(
                                                idUsuario = idUsuario,
                                                nombre = "",
                                                correo = notaOriginalEdicion?.usuario?.correo ?: "",
                                                contrasena = ""
                                            )
                                        )

                                        if (notaOriginalEdicion != null) {
                                            viewModel.editNota(notaOriginalEdicion!!, notaNueva)
                                            mensaje = "Nota actualizada"
                                        } else {
                                            viewModel.addNota(notaNueva)
                                            mensaje = "Nota guardada"
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
                                    } else {
                                        mensaje = "La nota no puede estar vacía"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = accent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    if (notaOriginalEdicion != null) "Actualizar" else "Guardar",
                                    color = onAccent,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            AnimatedVisibility(mensaje != null) {
                                mensaje?.let { mensajeActual ->
                                    Text(
                                        text = mensajeActual,
                                        color = if (mensajeActual.contains("vacía")) Color.Red else accent,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Tus notas",
                        color = secondaryHighlight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        "${notasFiltradas.size} ${if (notasFiltradas.size == 1) "nota" else "notas"}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    if (notasFiltradas.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No tienes notas aún.",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 32.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 8.dp)
                        ) {
                            items(notasFiltradas) { nota ->
                                NotaRapidaCard(
                                    nota = nota,
                                    accent = accent,
                                    onAccent = onAccent,
                                    cardColor = cardColor,
                                    onEliminar = {
                                        notaToDelete = nota
                                        showDeleteDialog = true
                                    },
                                    onEditar = {
                                        textoNota = nota.contenido
                                        esRecordatorio = nota.esRecordatorio
                                        recordatorioMinutos = ""
                                        notaOriginalEdicion = nota.copy()
                                    },
                                    onCopy = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Nota", nota.contenido)
                                        clipboard.setPrimaryClip(clip)
                                        showCopied = true
                                    }
                                )
                            }
                        }
                    }
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        textoNota = ""
                        esRecordatorio = false
                        recordatorioMinutos = ""
                        notaOriginalEdicion = null
                        mensaje = null
                    },
                    containerColor = accent,
                    contentColor = onAccent,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva nota")
                }
            }


            if (showDeleteDialog && notaToDelete != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.removeNota(notaToDelete!!.idNota)
                                if (notaOriginalEdicion?.idNota == notaToDelete!!.idNota) {
                                    textoNota = ""
                                    esRecordatorio = false
                                    recordatorioMinutos = ""
                                    notaOriginalEdicion = null
                                }
                                showDeleteDialog = false
                                notaToDelete = null
                            }
                        ) {
                            Text("Eliminar", color = Color(0xFFFF6B6B), fontWeight = FontWeight.SemiBold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar", color = accent, fontWeight = FontWeight.Medium)
                        }
                    },
                    title = {
                        Text("Eliminar Nota", color = accent, fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Text(
                            "¿Seguro que deseas eliminar esta nota? Esta acción no se puede deshacer.",
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                    },
                    containerColor = cardColor,
                    shape = RoundedCornerShape(16.dp)
                )
            }


            if (showCopied) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1300)
                    showCopied = false
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = accent.copy(alpha = 0.95f),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = onAccent, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("¡Nota copiada!", color = onAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun NotaRapidaCard(
    nota: NotaRapida,
    accent: Color,
    onAccent: Color,
    cardColor: Color,
    onEliminar: () -> Unit,
    onEditar: () -> Unit,
    onCopy: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = 8f
                shape = RoundedCornerShape(18.dp)
                clip = true
            }
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
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    nota.contenido,
                    color = onAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                if (nota.esRecordatorio) {
                    Text("🔔", fontSize = 16.sp)
                }
            }

            Text(
                nota.fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = accent)
                }
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = accent)
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFFF6B6B))
                }
            }
        }
    }
}


@OptIn(DelicateCoroutinesApi::class)
private fun programarNotificacionConDelay(context: Context, contenido: String, minutos: Long) {
    GlobalScope.launch {
        delay(TimeUnit.MINUTES.toMillis(minutos))
        enviarNotificacion(context, contenido)
    }
}

private fun crearCanalDeNotificacion(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val canal = NotificationChannel(
            "canal_rapidas",
            "Notas Rápidas",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal para notas como recordatorio"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(canal)
    }
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
private fun enviarNotificacion(context: Context, contenido: String) {
    val builder = NotificationCompat.Builder(context, "canal_rapidas")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("🔔 Recordatorio Nota")
        .setContentText(contenido)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    NotificationManagerCompat.from(context)
        .notify(System.currentTimeMillis().toInt(), builder.build())
}