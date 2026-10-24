package com.example.listafiltrable_compose

/**
 * Modelo de datos que representa la infraestructura universitaria.
 * @param id Identificador único para el rastreo de clave estable en Compose.
 * @param nombre Denominación oficial del edificio.
 * @param categoria Clasificación funcional (Servicios, Académico, Deportes, Eventos).
 * @param descripcion Detalle de las actividades o funciones que alberga.
 */
data class Edificio(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val descripcion: String
)