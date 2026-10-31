package com.example.persistenciaroom_compose

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EdificioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val categoria: String
)