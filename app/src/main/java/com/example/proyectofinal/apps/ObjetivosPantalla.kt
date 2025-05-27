package com.example.proyectofinal.apps

import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.ViewModel.ObjetivoViewModel
import com.example.proyectofinal.ViewModel.ProgresoObjetivoViewModel
import com.example.proyectofinal.model.Objetivo
import com.example.proyectofinal.model.Usuario
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjetivosPantalla(
    navController: NavController,
    idUsuario: Int,
    objetivoViewModel: ObjetivoViewModel = viewModel(),
    progresoViewModel: ProgresoObjetivoViewModel = viewModel()
) {

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf(LocalDate.now()) }
    var fechaFin by remember { mutableStateOf(LocalDate.now().plusDays(7)) }
    var completado by remember { mutableStateOf(false) }
    var editandoId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }
    var formularioError by remember { mutableStateOf<String?>(null) }
    var formVisible by remember { mutableStateOf(false) }


    val backgroundColor = Color(0xFF181818)
    val cardColor = Color(0xFF232232)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFB3B3B3)
    val accentColor = Color(0xFFB26DFF)
    val errorColor = Color(0xFFD32F2F)


    val objetivos by objetivoViewModel.objetivos.collectAsState()
    val isLoading by objetivoViewModel.isLoading.collectAsState()
    val error by objetivoViewModel.error.collectAsState()
    val progresosAll by progresoViewModel.progresos.collectAsState()

    val progresos = remember(progresosAll, objetivos) {
        progresosAll.filter { prog -> objetivos.any { it.idObjetivo == prog.objetivo.idObjetivo } }
    }
    val progresoManual: Map<Int, Int> = remember(progresos) {
        progresos.groupBy { it.objetivo.idObjetivo }.mapValues { it.value.size }
    }

    val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val context = LocalContext.current


    fun abrirDatePicker(fecha: LocalDate, onDateSelected: (LocalDate) -> Unit) {
        val dpd = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            fecha.year,
            fecha.monthValue - 1,
            fecha.dayOfMonth
        )
        dpd.show()
    }

    fun validarFormulario(): Boolean {
        when {
            titulo.isBlank() -> {
                formularioError = "El título es obligatorio"
                return false
            }
            descripcion.isBlank() -> {
                formularioError = "La descripción es obligatoria"
                return false
            }
            fechaFin.isBefore(fechaInicio) -> {
                formularioError = "La fecha fin no puede ser anterior a la fecha inicio"
                return false
            }
            else -> {
                formularioError = null
                return true
            }
        }
    }

    fun limpiarFormulario() {
        titulo = ""
        descripcion = ""
        fechaInicio = LocalDate.now()
        fechaFin = LocalDate.now().plusDays(7)
        completado = false
        editandoId = null
        formularioError = null
        formVisible = false
    }

    fun guardarObjetivo() {
        if (!validarFormulario()) return

        val objetivo = Objetivo(
            idObjetivo = editandoId ?: 0,
            titulo = titulo,
            descripcion = descripcion,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            completado = completado,
            usuario = Usuario(idUsuario, "", "", "")
        )

        objetivoViewModel.addObjetivo(objetivo)
        limpiarFormulario()
        Toast.makeText(
            context,
            if (editandoId == null) "Objetivo creado con éxito" else "Objetivo actualizado con éxito",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun calcularDiasTotales(objetivo: Objetivo): Int =
        ChronoUnit.DAYS.between(objetivo.fechaInicio, objetivo.fechaFin).toInt() + 1

    fun calcularPorcentajePorDia(objetivo: Objetivo): Float {
        val diasTotales = calcularDiasTotales(objetivo)
        return if (diasTotales > 0) 100f / diasTotales else 100f
    }

    fun obtenerProgresoManual(objetivoId: Int): Int = progresoManual[objetivoId] ?: 0

    fun calcularProgresoFinal(objetivo: Objetivo): Float {
        val progresoManualDias = obtenerProgresoManual(objetivo.idObjetivo)
        val diasTotales = calcularDiasTotales(objetivo)
        return (progresoManualDias.toFloat() / diasTotales).coerceIn(0f, 1f)
    }

    fun avanzarProgreso(objetivo: Objetivo) {
        val diasTotales = calcularDiasTotales(objetivo)
        val progresoActual = obtenerProgresoManual(objetivo.idObjetivo)
        if (!objetivo.completado && progresoActual < diasTotales) {
            progresoViewModel.agregarProgresoManual(objetivo)
            if (progresoActual + 1 == diasTotales) {
                objetivoViewModel.marcarObjetivoComoCompletado(objetivo.idObjetivo)
            }
        }
    }

    fun retrocederProgreso(objetivo: Objetivo) {
        val progresoActual = obtenerProgresoManual(objetivo.idObjetivo)
        if (!objetivo.completado && progresoActual > 0) {
            progresoViewModel.retrocederProgresoManual(objetivo)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Objetivos",
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("menu/$idUsuario") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = accentColor)
                    }
                },
                actions = {
                    IconButton(onClick = { formVisible = !formVisible }) {
                        Icon(
                            if (formVisible) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = if (formVisible) "Cerrar formulario" else "Nuevo objetivo",
                            tint = accentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor,
        snackbarHost = {
            if (error != null) {
                Snackbar(
                    action = {
                        TextButton(onClick = { objetivoViewModel.clearError() }) {
                            Text("OK", color = textPrimary)
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    containerColor = errorColor
                ) {
                    Text(error!!, color = textPrimary)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            AnimatedVisibility(
                visible = formVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            if (editandoId == null) "Nuevo Objetivo" else "Editar Objetivo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = accentColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        if (formularioError != null) {
                            Text(
                                formularioError!!,
                                color = errorColor,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título", color = accentColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = textSecondary,
                                cursorColor = accentColor
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción", color = accentColor) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = textSecondary,
                                cursorColor = accentColor
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DatePickerField(
                                label = "Fecha Inicio",
                                date = fechaInicio,
                                onDateChange = { fechaInicio = it },
                                textPrimary = textPrimary,
                                accentColor = accentColor,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            DatePickerField(
                                label = "Fecha Fin",
                                date = fechaFin,
                                onDateChange = { fechaFin = it },
                                textPrimary = textPrimary,
                                accentColor = accentColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = completado,
                                onCheckedChange = { completado = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = accentColor,
                                    uncheckedColor = textSecondary
                                )
                            )
                            Text(
                                "Marcar como completado",
                                color = textPrimary,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { guardarObjetivo() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text(if (editandoId == null) "Guardar" else "Actualizar", color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = accentColor)
                }
            } else {
                if (objetivos.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FlagCircle,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No hay objetivos creados",
                            color = textSecondary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        itemsIndexed(objetivos) { _, objetivo ->
                            val diasTotales = calcularDiasTotales(objetivo)
                            val porcentajePorDia = calcularPorcentajePorDia(objetivo)
                            val progresoActual = calcularProgresoFinal(objetivo)
                            val diasProgreso = obtenerProgresoManual(objetivo.idObjetivo)
                            val diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), objetivo.fechaFin).toInt()

                            val colorFondo = when {
                                objetivo.completado -> Color(0xFF263238)
                                diasRestantes < 0 -> Color(0xFF370B1E)
                                diasRestantes <= 3 -> Color(0xFF362037)
                                else -> cardColor
                            }

                            val colorBorde = when {
                                objetivo.completado -> Color(0xFF4CAF50)
                                diasRestantes < 0 -> Color(0xFFD32F2F)
                                diasRestantes <= 3 -> accentColor.copy(alpha = 0.6f)
                                else -> accentColor
                            }

                            val objetivoEstaCompleto = objetivo.completado || progresoActual >= 1f

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        titulo = objetivo.titulo
                                        descripcion = objetivo.descripcion
                                        fechaInicio = objetivo.fechaInicio
                                        fechaFin = objetivo.fechaFin
                                        completado = objetivo.completado
                                        editandoId = objetivo.idObjetivo
                                        formVisible = true
                                    },
                                colors = CardDefaults.cardColors(containerColor = colorFondo),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                border = androidx.compose.foundation.BorderStroke(2.dp, colorBorde)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                objetivo.titulo,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = accentColor
                                            )
                                            if (objetivo.descripcion.isNotBlank()) {
                                                Text(
                                                    objetivo.descripcion,
                                                    fontSize = 14.sp,
                                                    color = textSecondary,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                        }

                                        when {
                                            objetivo.completado -> ChipEstado("✓ Completado", Color(0xFF4CAF50))
                                            diasRestantes < 0 -> ChipEstado("⚠ Vencido", Color(0xFFD32F2F))
                                            diasRestantes <= 3 -> ChipEstado("⏰ Urgente", accentColor)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Inicio: ${formatoFecha.format(objetivo.fechaInicio)}",
                                            color = textSecondary,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            "Límite: ${formatoFecha.format(objetivo.fechaFin)}",
                                            color = textSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    // Progreso manual
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "Progreso Manual",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = accentColor
                                            )
                                            Text(
                                                "${(progresoActual * 100).toInt()}% ($diasProgreso/$diasTotales días)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (objetivo.completado) Color(0xFF4CAF50) else accentColor
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { progresoActual },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = when {
                                                objetivo.completado -> Color(0xFF4CAF50)
                                                progresoActual >= 1f -> Color(0xFF4CAF50)
                                                diasRestantes < 0 -> Color(0xFFD32F2F)
                                                diasRestantes <= 3 -> accentColor.copy(alpha = 0.7f)
                                                else -> accentColor
                                            },
                                            trackColor = Color(0xFF292929)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "Avance: ${String.format("%.1f", porcentajePorDia)}% por día",
                                                fontSize = 10.sp,
                                                color = textSecondary,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Row {
                                                FilledTonalButton(
                                                    onClick = { retrocederProgreso(objetivo) },
                                                    enabled = !objetivoEstaCompleto && diasProgreso > 0,
                                                    modifier = Modifier.size(32.dp),
                                                    contentPadding = PaddingValues(0.dp),
                                                    colors = ButtonDefaults.filledTonalButtonColors(
                                                        containerColor = errorColor
                                                    ),
                                                    shape = CircleShape
                                                ) {
                                                    Text(
                                                        "−",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                FilledTonalButton(
                                                    onClick = { avanzarProgreso(objetivo) },
                                                    enabled = !objetivoEstaCompleto && diasProgreso < diasTotales,
                                                    modifier = Modifier.size(32.dp),
                                                    contentPadding = PaddingValues(0.dp),
                                                    colors = ButtonDefaults.filledTonalButtonColors(
                                                        containerColor = Color(0xFF4CAF50)
                                                    ),
                                                    shape = CircleShape
                                                ) {
                                                    Text(
                                                        "+",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            when {
                                                progresoActual >= 1f -> "🎉 ¡Objetivo completado manualmente!"
                                                diasProgreso == 0 -> "▶️ Comienza tu progreso"
                                                diasProgreso == diasTotales - 1 -> "🔥 ¡Casi terminado!"
                                                else -> "💪 Progreso: $diasProgreso de $diasTotales días completados"
                                            },
                                            fontSize = 11.sp,
                                            color = when {
                                                progresoActual >= 1f -> Color(0xFF4CAF50)
                                                diasProgreso > diasTotales * 0.7 -> Color(0xFF4CAF50)
                                                diasProgreso > diasTotales * 0.4 -> accentColor
                                                else -> textSecondary
                                            },
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        IconButton(
                                            onClick = {
                                                titulo = objetivo.titulo
                                                descripcion = objetivo.descripcion
                                                fechaInicio = objetivo.fechaInicio
                                                fechaFin = objetivo.fechaFin
                                                completado = objetivo.completado
                                                editandoId = objetivo.idObjetivo
                                                formVisible = true
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Editar",
                                                tint = accentColor,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { showDeleteDialog = objetivo.idObjetivo }
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = errorColor,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar objetivo", color = accentColor) },
            text = { Text("¿Estás seguro de que deseas eliminar este objetivo?", color = textSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog?.let {
                            objetivoViewModel.removeObjetivo(it)
                            if (editandoId == it) limpiarFormulario()
                        }
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar", color = errorColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar", color = accentColor)
                }
            },
            containerColor = cardColor
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerField(
    label: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    textPrimary: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    Card(
        modifier = modifier
            .clickable {
                val dpd = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        onDateChange(LocalDate.of(year, month + 1, dayOfMonth))
                    },
                    date.year,
                    date.monthValue - 1,
                    date.dayOfMonth
                )
                dpd.show()
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF232232)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                fontSize = 12.sp,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                formatoFecha.format(date),
                fontSize = 14.sp,
                color = textPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ChipEstado(text: String, color: Color) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier.padding(start = 6.dp)
    ) {
        Text(
            text,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}