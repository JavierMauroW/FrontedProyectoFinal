package com.example.proyectofinal.apps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectofinal.ViewModel.ProgresoObjetivoViewModel
import com.example.proyectofinal.ViewModel.ObjetivoViewModel
import com.example.proyectofinal.model.Objetivo
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgresoObjetivosPantalla(
    navController: NavController,
    idUsuario: Int,
    objetivoViewModel: ObjetivoViewModel = viewModel(),
    progresoViewModel: ProgresoObjetivoViewModel = viewModel()
) {

    val backgroundColor = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFB3B3B3)
    val accentColor = Color(0xFF9C27B0)
    val greenColor = Color(0xFF4CAF50)
    val errorColor = Color(0xFFE57373)
    val warningColor = Color(0xFFFF9800)

    val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")


    val objetivosAll by objetivoViewModel.objetivos.collectAsState()
    val progresosAll by progresoViewModel.progresos.collectAsState()

    val objetivos = remember(objetivosAll, idUsuario) {
        objetivosAll.filter { it.usuario.idUsuario == idUsuario }
    }
    val progresos = remember(progresosAll, objetivos) {
        progresosAll.filter { prog -> objetivos.any { it.idObjetivo == prog.objetivo.idObjetivo } }
    }
    val progresoManual: Map<Int, Int> = remember(progresos) {
        progresos.groupBy { it.objetivo.idObjetivo }.mapValues { it.value.size }
    }

    fun calcularDiasTotales(objetivo: Objetivo): Int {
        val dias = ChronoUnit.DAYS.between(objetivo.fechaInicio, objetivo.fechaFin).toInt() + 1
        return if (dias > 0) dias else 1
    }
    fun obtenerProgresoManual(objetivoId: Int): Int = progresoManual[objetivoId] ?: 0
    fun calcularProgresoFinal(objetivo: Objetivo): Float {
        val progresoManualDias = obtenerProgresoManual(objetivo.idObjetivo)
        val diasTotales = calcularDiasTotales(objetivo)
        return if (diasTotales > 0) (progresoManualDias.toFloat() / diasTotales).coerceIn(0f, 1f) else 0f
    }

    val totalObjetivos = objetivos.size
    val objetivosCompletados = objetivos.count { it.completado }
    val objetivosEnProgreso = objetivos.count { !it.completado }
    val progresoPromedio = if (objetivos.isNotEmpty()) {
        objetivos.map { calcularProgresoFinal(it) }.average().toFloat()
    } else 0f

    LaunchedEffect(idUsuario) {
        objetivoViewModel.loadObjetivos()
        progresoViewModel.loadProgresos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Progreso de Objetivos",
                        color = textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp, horizontal = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "📊 Progreso de Objetivos",
                            fontSize = 21.sp,
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Visualiza tu progreso y estadísticas",
                            color = textSecondary,
                            fontSize = 15.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))


                if (totalObjetivos <= 1) {
                    EstadisticaCard(
                        icon = "📋",
                        valor = "$totalObjetivos",
                        label = "Total",
                        colorValor = accentColor,
                        cardColor = cardColor,
                        textSecondary = textSecondary,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EstadisticaCard(
                            icon = "📋",
                            valor = "$totalObjetivos",
                            label = "Total",
                            colorValor = accentColor,
                            cardColor = cardColor,
                            textSecondary = textSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        EstadisticaCard(
                            icon = "✅",
                            valor = "$objetivosCompletados",
                            label = "Completados",
                            colorValor = greenColor,
                            cardColor = cardColor,
                            textSecondary = textSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        EstadisticaCard(
                            icon = "⏳",
                            valor = "$objetivosEnProgreso",
                            label = "En Progreso",
                            colorValor = warningColor,
                            cardColor = cardColor,
                            textSecondary = textSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))


                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "🎯 Progreso Promedio",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                            Text(
                                "${(progresoPromedio * 100).toInt()}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accentColor
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progresoPromedio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = accentColor,
                            trackColor = Color(0xFF232323)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))


                Text(
                    "📋 Detalle de Objetivos",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        fontSize = 19.sp
                    ),
                    modifier = Modifier.padding(start = 2.dp, top = 3.dp, bottom = 7.dp)
                )

                Box(
                    modifier = Modifier.weight(1f, fill = true)
                ) {
                    if (objetivos.isNotEmpty()) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(objetivos) { objetivo ->
                                val diasTotales = calcularDiasTotales(objetivo)
                                val progresoActual = calcularProgresoFinal(objetivo)
                                val diasProgreso = obtenerProgresoManual(objetivo.idObjetivo)
                                val diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), objetivo.fechaFin).toInt()

                                val colorFondo = when {
                                    objetivo.completado -> Color(0xFF263238)
                                    diasRestantes < 0 -> errorColor
                                    diasRestantes <= 3 -> warningColor
                                    else -> cardColor
                                }
                                val colorBorde = when {
                                    objetivo.completado -> greenColor
                                    diasRestantes < 0 -> errorColor
                                    diasRestantes <= 3 -> warningColor
                                    else -> accentColor
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = colorFondo),
                                    shape = RoundedCornerShape(11.dp),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, colorBorde),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(modifier = Modifier.padding(13.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    objetivo.titulo,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = accentColor
                                                )
                                                if (objetivo.descripcion.isNotBlank()) {
                                                    Text(
                                                        objetivo.descripcion,
                                                        fontSize = 13.sp,
                                                        color = textSecondary,
                                                        modifier = Modifier.padding(top = 2.dp)
                                                    )
                                                }
                                            }
                                            if (objetivo.completado) {
                                                EstadoBadge("✓ Completado", greenColor)
                                            } else if (diasRestantes < 0) {
                                                EstadoBadge("⚠ Vencido", errorColor)
                                            } else if (diasRestantes <= 3) {
                                                EstadoBadge("⏰ Urgente", warningColor)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "📅 Inicio: ${formatoFecha.format(objetivo.fechaInicio)}",
                                                color = textSecondary,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                "🎯 Límite: ${formatoFecha.format(objetivo.fechaFin)}",
                                                color = textSecondary,
                                                fontSize = 12.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(7.dp))

                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    "📊 Progreso",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = accentColor
                                                )
                                                Text(
                                                    "${(progresoActual * 100).toInt()}% ($diasProgreso/$diasTotales días)",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (objetivo.completado) greenColor else accentColor
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(3.dp))

                                            LinearProgressIndicator(
                                                progress = { progresoActual },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(7.dp)
                                                    .clip(RoundedCornerShape(4.dp)),
                                                color = when {
                                                    objetivo.completado -> greenColor
                                                    progresoActual >= 1f -> greenColor
                                                    diasRestantes < 0 -> errorColor
                                                    diasRestantes <= 3 -> warningColor
                                                    else -> accentColor
                                                },
                                                trackColor = Color(0xFF232323)
                                            )

                                            Spacer(modifier = Modifier.height(5.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    "⏱️ Restan: ${if (diasRestantes >= 0) diasRestantes else "Vencido"} días",
                                                    fontSize = 11.sp,
                                                    color = textSecondary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    "📈 Duración: $diasTotales días",
                                                    fontSize = 11.sp,
                                                    color = textSecondary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 40.dp, bottom = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "📋",
                                fontSize = 48.sp,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Text(
                                "No hay objetivos registrados",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Crea tu primer objetivo para ver el progreso aquí",
                                fontSize = 14.sp,
                                color = textSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun EstadisticaCard(
    icon: String,
    valor: String,
    label: String,
    colorValor: Color,
    cardColor: Color,
    textSecondary: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(11.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 11.dp, horizontal = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 28.sp)
            Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colorValor)
            Text(label, fontSize = 13.sp, color = textSecondary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun EstadoBadge(text: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(7.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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