
package com.example.sincronizacionservicio_compose.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EdificioDao {

    @Insert
    suspend fun insertarEdificios(
        edificios: List<EdificioEntity>
    )

    @Update
    suspend fun actualizarEdificio(
        edificio: EdificioEntity
    )

    @Query("SELECT * FROM EdificioEntity ORDER BY id")
    suspend fun obtenerTodos(): List<EdificioEntity>

    @Query("""
        SELECT * FROM EdificioEntity
        WHERE categoria = 'Académico'
        AND sincronizado = 0
    """)
    suspend fun obtenerPendientes(): List<EdificioEntity>

    @Query("SELECT COUNT(*) FROM EdificioEntity")
    suspend fun contarEdificios(): Int

    @Query("""
        SELECT COUNT(*) FROM EdificioEntity
        WHERE sincronizado = 1
    """)
    suspend fun contarSincronizados(): Int
}
