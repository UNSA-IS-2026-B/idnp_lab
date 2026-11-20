package com.example.audioguiaforeground_compose.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReproduccionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarUltimaReproduccion(
        reproduccion: UltimaReproduccionEntity
    )

    @Query("""
        SELECT * FROM ultima_reproduccion
        WHERE id = 1
        LIMIT 1
    """)
    suspend fun obtenerUltimaReproduccion():
            UltimaReproduccionEntity?

    @Query("DELETE FROM ultima_reproduccion")
    suspend fun borrarHistorial()
}
