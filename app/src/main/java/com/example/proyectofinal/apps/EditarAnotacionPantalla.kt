package com.example.proyectofinal.apps

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectofinal.ViewModel.AnotacionPersonalViewModel
import com.example.proyectofinal.model.AnotacionPersonal
import com.example.proyectofinal.model.Usuario
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.roundToInt

data class ChecklistItem(
    val text: String,
    val isChecked: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditarAnotacionPantalla(
    navController: NavController,
    idUsuario: Int,
    idAnotacion: Int?,
    viewModel: AnotacionPersonalViewModel = viewModel()
) {

    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var checklistItems by remember { mutableStateOf(listOf<ChecklistItem>()) }
    var isChecklistMode by remember { mutableStateOf(false) }
    var currentFontSize by remember { mutableStateOf(16) }
    var currentTextColor by remember { mutableStateOf(Color(0xFF2C3E50)) }
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderlined by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showFontSizePicker by remember { mutableStateOf(false) }
    var darkMode by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // Para animación de página
    var pageOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var dragStartX by remember { mutableStateOf(0f) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val anotaciones by viewModel.anotaciones.collectAsState()


    val fondoColor = if (darkMode) Color(0xFF181818) else Color(0xFFF8FAFC)
    val cardColor = if (darkMode) Color(0xFF232232) else Color(0xFFF3F3FF)
    val primaryColor = if (darkMode) Color(0xFF2C3E50) else Color(0xFF1a1033)
    val accentColor = if (darkMode) Color(0xFFB26DFF) else Color(0xFF7C3AED)
    val textPrimary = if (darkMode) Color.White else Color(0xFF232232)
    val textSecondary = if (darkMode) Color(0xFFB3B3B3) else Color(0xFF6B7280)
    val borderColor = if (darkMode) Color(0xFF353535) else Color(0xFFD1D5DB)
    val lineColor = if (darkMode) Color(0xFF282A36).copy(alpha = 0.5f) else Color(0xFFD1D9E6)
    val marginColor = accentColor
    val lineSpacing = 28.dp

    val availableColors = listOf(
        primaryColor, accentColor, Color(0xFFE74C3C),
        Color(0xFF3498DB), Color(0xFF27AE60), Color(0xFF9B59B6),
        Color(0xFFF39C12), Color(0xFF1ABC9C), Color(0xFF34495E)
    )

    LaunchedEffect(idAnotacion) {
        if (idAnotacion != null && idAnotacion != -1) {
            val nota = viewModel.getAnotacionById(idAnotacion)
            nota?.let {
                titulo = it.titulo
                if (it.contenido.contains("☑") || it.contenido.contains("☐")) {
                    val items = it.contenido.lines().filter { l -> l.isNotBlank() }.map { line ->
                        val checked = line.trim().startsWith("☑")
                        val text = line.dropWhile { c -> c == '☑' || c == '☐' || c.isWhitespace() }
                        ChecklistItem(text, checked)
                    }
                    checklistItems = items
                    isChecklistMode = true
                } else {
                    contenido = it.contenido
                    isChecklistMode = false
                }
            }
        }
    }


    val listaValida = checklistItems.any { it.text.isNotBlank() }
    val tituloDuplicado = anotaciones.any {
        it.titulo.equals(titulo, ignoreCase = true) && it.idAnotacion != (idAnotacion ?: 0)
    }


    fun exportarComoPDF(context: Context, title: String, content: String, density: Density) {
        val pageWidth = with(density) { 400.dp.toPx() }
        val pageHeight = with(density) { 600.dp.toPx() }

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth.toInt(), pageHeight.toInt(), 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas


        canvas.drawColor(if (darkMode) 0xFF181818.toInt() else 0xFFF8FAFC.toInt())


        val paintLine = android.graphics.Paint().apply {
            color = if (darkMode) 0xFF282A36.toInt() else 0xFFD1D9E6.toInt()
            strokeWidth = 3f
        }
        val paintMargin = android.graphics.Paint().apply {
            color = accentColor.value.toInt()
            strokeWidth = 5f
        }

        var y = 80f
        while (y < pageHeight) {
            canvas.drawLine(0f, y, pageWidth, y, paintLine)
            y += 45f
        }
        canvas.drawLine(60f, 0f, 60f, pageHeight, paintMargin)
        canvas.drawLine(0f, 80f, pageWidth, 80f, paintLine)


        val paintText = android.graphics.Paint().apply {
            isAntiAlias = true
            color = if (darkMode) 0xFFFFFFFF.toInt() else 0xFF232232.toInt()
            textSize = 18f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        val paintContent = android.graphics.Paint().apply {
            isAntiAlias = true
            color = if (darkMode) 0xFFB3B3B3.toInt() else 0xFF6B7280.toInt()
            textSize = 15f
            typeface = android.graphics.Typeface.DEFAULT
        }

        canvas.drawText(title, 80f, 60f, paintText)
        var contentY = 110f
        for (line in content.split("\n")) {
            canvas.drawText(line, 80f, contentY, paintContent)
            contentY += 45f
        }

        pdfDocument.finishPage(page)

        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        dir?.mkdirs()
        val file = File(dir, "${title.take(20)}.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        Toast.makeText(context, "PDF exportado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(fondoColor)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        dragStartX = offset.x
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        pageOffset = (pageOffset + dragAmount).coerceIn(-120f, 0f)
                    },
                    onDragEnd = {
                        isDragging = false
                        if (abs(pageOffset) > 60f) {
                            scope.launch {
                                for (step in 0..20) {
                                    pageOffset = -120f + (120f * (20 - step) / 20f)
                                    kotlinx.coroutines.delay(3)
                                }
                                pageOffset = 0f
                            }
                        } else {
                            pageOffset = 0f
                        }
                    }
                )
            }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(pageOffset.roundToInt(), 0) }
                .drawBehind {
                    val spacingPx = lineSpacing.toPx()
                    val marginX = 60.dp.toPx()
                    var y = 80.dp.toPx()


                    while (y < size.height - 56.dp.toPx()) {
                        drawLine(
                            color = lineColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.2.dp.toPx()
                        )
                        y += spacingPx
                    }


                    drawLine(
                        color = marginColor,
                        start = Offset(marginX, 80.dp.toPx()),
                        end = Offset(marginX, size.height - 56.dp.toPx()),
                        strokeWidth = 2.5.dp.toPx()
                    )
                }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                if (idAnotacion != null && idAnotacion != -1) "Editar Nota" else "Nueva Nota",
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = accentColor)
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    darkMode = !darkMode
                                }
                            ) {
                                Icon(
                                    if (darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (darkMode) "Modo claro" else "Modo oscuro",
                                    tint = accentColor
                                )
                            }
                            IconButton(
                                onClick = {
                                    val titleExport = titulo.ifBlank { "Nota" }
                                    val contentExport = if (isChecklistMode) {
                                        checklistItems
                                            .filter { it.text.isNotBlank() }
                                            .joinToString("\n") { "${if (it.isChecked) "☑" else "☐"} ${it.text}" }
                                    } else contenido
                                    exportarComoPDF(context, titleExport, contentExport, density)
                                }
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "Exportar PDF", tint = accentColor)
                            }
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = fondoColor
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            if (tituloDuplicado) {
                                Toast.makeText(context, "Ya existe una nota con ese título", Toast.LENGTH_SHORT).show()
                                return@FloatingActionButton
                            }
                            if (titulo.isNotBlank() && ((isChecklistMode && listaValida) || (!isChecklistMode && contenido.isNotBlank()))) {
                                val finalContent = if (isChecklistMode) {
                                    checklistItems
                                        .filter { it.text.isNotBlank() }
                                        .joinToString("\n") { "${if (it.isChecked) "☑" else "☐"} ${it.text}" }
                                } else {
                                    contenido
                                }
                                val anotacion = AnotacionPersonal(
                                    idAnotacion = idAnotacion ?: 0,
                                    titulo = titulo,
                                    contenido = finalContent,
                                    fechaHora = LocalDateTime.now(),
                                    usuario = Usuario(idUsuario, "", "", "")
                                )
                                if (idAnotacion != null && idAnotacion != -1) {
                                    viewModel.editAnotacion(anotacion)
                                } else {
                                    viewModel.addAnotacion(anotacion)
                                }
                                viewModel.loadAnotaciones()
                                Toast.makeText(
                                    context,
                                    if (idAnotacion != null && idAnotacion != -1) "Nota actualizada" else "Nota guardada",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Completa los campos", Toast.LENGTH_SHORT).show()
                            }
                        },
                        containerColor = accentColor
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Guardar", tint = Color.White)
                    }
                },
                bottomBar = {
                    Surface(
                        color = cardColor,
                        shadowElevation = 10.dp,
                        modifier = Modifier.height(56.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(
                                onClick = {
                                    isChecklistMode = !isChecklistMode
                                    if (isChecklistMode && checklistItems.isEmpty()) {
                                        checklistItems = listOf(ChecklistItem("", false))
                                    }
                                    if (!isChecklistMode) checklistItems = listOf()
                                }
                            ) {
                                Icon(
                                    Icons.Default.CheckBox,
                                    contentDescription = "Checklist",
                                    tint = if (isChecklistMode) accentColor else textSecondary
                                )
                            }

                            IconButton(onClick = { isBold = !isBold }) {
                                Icon(
                                    Icons.Default.FormatBold,
                                    contentDescription = "Negrita",
                                    tint = if (isBold) accentColor else textSecondary
                                )
                            }
                            IconButton(onClick = { isItalic = !isItalic }) {
                                Icon(
                                    Icons.Default.FormatItalic,
                                    contentDescription = "Cursiva",
                                    tint = if (isItalic) accentColor else textSecondary
                                )
                            }
                            IconButton(onClick = { isUnderlined = !isUnderlined }) {
                                Icon(
                                    Icons.Default.FormatUnderlined,
                                    contentDescription = "Subrayado",
                                    tint = if (isUnderlined) accentColor else textSecondary
                                )
                            }
                            IconButton(onClick = { showColorPicker = true }) {
                                Icon(
                                    Icons.Default.Palette,
                                    contentDescription = "Color",
                                    tint = currentTextColor
                                )
                            }
                            IconButton(onClick = { showFontSizePicker = true }) {
                                Icon(
                                    Icons.Default.FormatSize,
                                    contentDescription = "Tamaño",
                                    tint = accentColor
                                )
                            }
                            Text(
                                text = "$currentFontSize",
                                color = accentColor,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                containerColor = Color.Transparent
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(start = 72.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                ) {

                    if (isLoading) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color(0x88000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = accentColor)
                        }
                    }

                    if (error != null) {
                        Text(
                            text = error ?: "",
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        BasicTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            textStyle = TextStyle(
                                fontSize = 20.sp,
                                color = textPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(accentColor),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    if (titulo.isEmpty()) {
                                        Text(
                                            "Título de la nota...",
                                            style = TextStyle(
                                                fontSize = 20.sp,
                                                color = textSecondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Crossfade(targetState = isChecklistMode, label = "ChecklistMode") { checklist ->
                            if (checklist) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    itemsIndexed(checklistItems) { index, item ->
                                        ChecklistItemView(
                                            item = item,
                                            onItemChange = { newItem ->
                                                checklistItems = checklistItems.toMutableList().apply {
                                                    set(index, newItem)
                                                }
                                            },
                                            onDelete = {
                                                checklistItems = checklistItems.toMutableList().apply {
                                                    removeAt(index)
                                                }
                                                if (checklistItems.isEmpty()) {
                                                    isChecklistMode = false
                                                }
                                            },
                                            textStyle = TextStyle(
                                                fontSize = currentFontSize.sp,
                                                color = currentTextColor,
                                                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                                fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                                                textDecoration = if (isUnderlined) TextDecoration.Underline else TextDecoration.None
                                            )
                                        )
                                    }
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    checklistItems = checklistItems + ChecklistItem("", false)
                                                }
                                                .padding(vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Agregar",
                                                tint = accentColor.copy(alpha = 0.7f)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Agregar elemento",
                                                color = accentColor.copy(alpha = 0.7f),
                                                fontSize = currentFontSize.sp
                                            )
                                        }
                                    }
                                }
                            } else {
                                BasicTextField(
                                    value = contenido,
                                    onValueChange = { contenido = it },
                                    textStyle = TextStyle(
                                        fontSize = currentFontSize.sp,
                                        color = currentTextColor,
                                        lineHeight = 28.sp,
                                        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                        fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                                        textDecoration = if (isUnderlined) TextDecoration.Underline else TextDecoration.None
                                    ),
                                    cursorBrush = SolidColor(currentTextColor),
                                    modifier = Modifier.fillMaxSize(),
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(4.dp)
                                        ) {
                                            if (contenido.isEmpty()) {
                                                Text(
                                                    "Escribe aquí el contenido de tu nota...",
                                                    style = TextStyle(
                                                        fontSize = currentFontSize.sp,
                                                        color = textSecondary,
                                                        lineHeight = 28.sp
                                                    )
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }


        if (showColorPicker) {
            AlertDialog(
                onDismissRequest = { showColorPicker = false },
                title = { Text("Seleccionar color", color = accentColor) },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(availableColors.size) { idx ->
                                val color = availableColors[idx]
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(color, shape = CircleShape)
                                        .border(
                                            3.dp,
                                            if (color == currentTextColor) accentColor else Color.Transparent,
                                            CircleShape
                                        )
                                        .clickable {
                                            currentTextColor = color
                                            showColorPicker = false
                                        }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showColorPicker = false }) {
                        Text("Cerrar", color = accentColor)
                    }
                }
            )
        }

        if (showFontSizePicker) {
            AlertDialog(
                onDismissRequest = { showFontSizePicker = false },
                title = { Text("Tamaño de fuente", color = accentColor) },
                text = {
                    Column {
                        listOf(12, 14, 16, 18, 20, 22, 24, 28, 32).forEach { size ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentFontSize = size
                                        showFontSizePicker = false
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentFontSize == size,
                                    onClick = {
                                        currentFontSize = size
                                        showFontSizePicker = false
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = accentColor,
                                        unselectedColor = textSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${size}sp",
                                    fontSize = size.sp,
                                    color = if (currentFontSize == size) accentColor else textSecondary
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFontSizePicker = false }) {
                        Text("Cerrar", color = accentColor)
                    }
                }
            )
        }
    }
}

@Composable
fun ChecklistItemView(
    item: ChecklistItem,
    onItemChange: (ChecklistItem) -> Unit,
    onDelete: () -> Unit,
    textStyle: TextStyle
) {
    val accentColor = Color(0xFFB26DFF)
    val textSecondary = Color(0xFFB3B3B3)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = {
                onItemChange(item.copy(isChecked = it))
            },
            colors = CheckboxDefaults.colors(
                checkedColor = accentColor,
                uncheckedColor = textSecondary
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = item.text,
            onValueChange = {
                onItemChange(item.copy(text = it))
            },
            textStyle = textStyle.copy(
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else textStyle.textDecoration
            ),
            cursorBrush = SolidColor(textStyle.color),
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            decorationBox = { innerTextField ->
                if (item.text.isEmpty()) {
                    Text(
                        "Nuevo elemento",
                        style = textStyle.copy(
                            color = textSecondary
                        )
                    )
                }
                innerTextField()
            }
        )

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Eliminar",
                tint = Color.Red,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}