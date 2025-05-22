package com.example.proyectofinal.model



import java.time.LocalDateTime

data class AnotacionPersonal(
    val idAnotacion: Int,
    val titulo: String,
    val contenido: String,
    val fechaHora: LocalDateTime,
    val usuario: Usuario,
)

