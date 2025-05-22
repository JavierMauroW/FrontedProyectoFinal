package com.example.proyectofinal.model



import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("idUsuario")
    val idUsuario: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("contrasena")
    val contrasena: String
)
