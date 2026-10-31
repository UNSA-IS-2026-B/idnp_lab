package com.example.persistenciaroom_compose

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AmbienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["ambienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NotaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ambienteId: Int,
    val texto: String
)