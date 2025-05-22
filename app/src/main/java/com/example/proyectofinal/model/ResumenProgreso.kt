package com.example.proyectofinal.model



import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class ResumenProgreso(
    @SerializedName("idResumen")
    val idResumen: Int,

    @SerializedName("fecha")
    val fecha: LocalDate,

    @SerializedName("objetivosCompletados")
    val objetivosCompletados: Int,

    @SerializedName("totalObjetivos")
    val totalObjetivos: Int,

    @SerializedName("comentarioResumen")
    val comentarioResumen: String,

    @SerializedName("usuario")
    val usuario: Usuario
)

