package com.example.proyectofinal.apps

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectofinal.ViewModel.AnotacionPersonalViewModel
import com.example.proyectofinal.model.AnotacionPersonal
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnotacionesPersonalesPantalla(
    navController: NavController,
    idUsuario: Int,
    viewModel: AnotacionPersonalViewModel = viewModel()
) {
    val context = LocalContext.current
    val anotaciones by viewModel.anotaciones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var mostrarBusqueda by remember { mutableStateOf(false) }
    var textoBusqueda by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Paleta profesional y optimizada
    val backgroundColor = Brush.verticalGradient(
        listOf(Color(0xFF181818), Color(0xFF1a1033))
    )
    val cardColor = Color(0xFF232232)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFB3B3B3)
    val accentColor = Color(0xFFB26DFF)
    val borderColor = Color(0xFF353535)

    LaunchedEffect(Unit) {
        viewModel.loadAnotaciones()
    }


    val anotacionesFiltradas = remember(anotaciones, textoBusqueda) {
        if (textoBusqueda.isBlank()) {
            anotaciones
        } else {
            anotaciones.filter { nota ->
                nota.titulo.contains(textoBusqueda, ignoreCase = true) ||
                        nota.contenido.contains(textoBusqueda, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            if (mostrarBusqueda) {

                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { textoBusqueda = it },
                            placeholder = { Text("Buscar notas...", color = textSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = borderColor
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            mostrarBusqueda = false
                            textoBusqueda = ""
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Cerrar búsqueda", tint = textPrimary)
                        }
                    },
                    actions = {
                        if (textoBusqueda.isNotEmpty()) {
                            IconButton(onClick = { textoBusqueda = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = textPrimary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            } else {

                TopAppBar(
                    title = {
                        Text(
                            "Mis Anotaciones",
                            color = textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate("menu/$idUsuario") {
                                    popUpTo("menu/$idUsuario") { inclusive = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = accentColor)
                        }
                    },
                    actions = {
                        IconButton(onClick = { mostrarBusqueda = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = accentColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("agregar_anotacion/$idUsuario") },
                containerColor = accentColor,
                shape = CircleShape,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Nueva nota",
                    tint = Color.White
                )
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Total: ${anotacionesFiltradas.size}",
                        color = accentColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.weight(1f))
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = accentColor,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))

                when {
                    error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(error ?: "", color = Color.Red, fontSize = 16.sp)
                        }
                    }
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = accentColor)
                        }
                    }
                    anotaciones.isEmpty() && textoBusqueda.isBlank() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.NoteAdd,
                                    contentDescription = null,
                                    tint = textSecondary,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Aún no tienes notas",
                                    color = textSecondary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Pulsa el botón + para crear tu primera nota",
                                    color = textSecondary,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    anotacionesFiltradas.isEmpty() && textoBusqueda.isNotBlank() -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = textSecondary,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No se encontraron notas",
                                    color = textSecondary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Intenta con otros términos de búsqueda",
                                    color = textSecondary,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {

                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalItemSpacing = 12.dp,
                            contentPadding = PaddingValues(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(anotacionesFiltradas, key = { it.idAnotacion }) { nota ->
                                NotaCard(
                                    nota = nota,
                                    onClick = {
                                        navController.navigate("agregar_anotacion/$idUsuario/${nota.idAnotacion}")
                                    },
                                    onDelete = {
                                        viewModel.removeAnotacion(nota.idAnotacion)
                                        snackbarMessage = "Nota eliminada"
                                        showSnackbar = true
                                    },
                                    cardColor = cardColor,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showSnackbar,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            ) {
                Snackbar(
                    containerColor = accentColor,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("Cerrar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                ) {
                    Text(snackbarMessage)
                }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1500)
                    showSnackbar = false
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotaCard(
    nota: AnotacionPersonal,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    cardColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 0.97f else 1f, label = "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = 8f
                shape = RoundedCornerShape(14.dp)
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
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header con fecha y menú
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = nota.fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    color = textSecondary,
                    fontSize = 11.sp
                )

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Más opciones",
                            tint = accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(cardColor)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = accentColor
                                    )
                                    Text("Editar", color = accentColor)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onClick()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color.Red
                                    )
                                    Text("Eliminar", color = Color.Red)
                                }
                            },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
            ) {
                if (nota.titulo.isNotBlank()) {
                    Text(
                        text = nota.titulo,
                        color = textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = nota.contenido,
                    color = textSecondary,
                    fontSize = 12.sp,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar nota", color = accentColor) },
            text = { Text("¿Estás seguro de que quieres eliminar esta nota?", color = textSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = accentColor)
                }
            },
            containerColor = cardColor
        )
    }
}