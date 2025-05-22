package com.example.proyectofinal.model



import com.google.gson.annotations.SerializedName
import java.time.LocalDate


data class NotaRapida(
    @SerializedName("idNota")
    val idNota: Int,

    @SerializedName("contenido")
    val contenido: String,

    @SerializedName("fechaCreacion")
    val fechaCreacion: LocalDate,

    @SerializedName("esRecordatorio")
    val esRecordatorio: Boolean,

    @SerializedName("usuario")
    val usuario: Usuario
)
