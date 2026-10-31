package com.example.persistenciaroom_compose

import androidx.room.Embedded
import androidx.room.Relation

data class AmbienteConNotas(
    @Embedded val ambiente: AmbienteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "ambienteId"
    )
    val notas: List<NotaEntity>
)