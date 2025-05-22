package com.example.proyectofinal.model


import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class ProgresoObjetivo(
    @SerializedName("idProgreso")
    val idProgreso: Int,

    @SerializedName("fecha")
    val fecha: LocalDate,

    @SerializedName("progresoDiario")
    val progresoDiario: String,

    @SerializedName("comentario")
    val comentario: String,

    @SerializedName("objetivo")
    val objetivo: Objetivo
)
