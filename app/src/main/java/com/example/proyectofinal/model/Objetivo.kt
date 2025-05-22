package com.example.proyectofinal.model



import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Objetivo(
    @SerializedName("idObjetivo")
    val idObjetivo: Int,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("fechaInicio")
    val fechaInicio: LocalDate,

    @SerializedName("fechaFin")
    val fechaFin: LocalDate,

    @SerializedName("completado")
    val completado: Boolean,

    @SerializedName("usuario")
    val usuario: Usuario
)
