package com.example.lab10.data

import com.google.gson.annotations.SerializedName

data class EspacioModel(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("ubicacion")
    val ubicacion: String,

    @SerializedName("aforo_maximo")
    val aforoMaximo: Int,

    @SerializedName("estado_operativo")
    val estadoOperativo: Boolean = true
)