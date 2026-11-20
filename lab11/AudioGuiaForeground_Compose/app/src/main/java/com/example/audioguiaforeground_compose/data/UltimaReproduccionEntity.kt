package com.example.audioguiaforeground_compose.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ultima_reproduccion")
data class UltimaReproduccionEntity(
    @PrimaryKey
    val id: Int = 1,
    val edificioId: Int,
    val nombreEdificio: String
)
