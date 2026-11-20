package com.example.audioguiaforeground_compose

data class EdificioCampus(
    val id: Int,
    val nombre: String
)

object EdificiosCampus {

    val lista = listOf(
        EdificioCampus(1, "Escuela Profesional de Ingeniería de Sistemas"),
        EdificioCampus(2, "Biblioteca de Ingenierías"),
        EdificioCampus(3, "Facultad de Arquitectura"),
        EdificioCampus(4, "Escuela Profesional de Ingeniería Civil"),
        EdificioCampus(5, "Escuela Profesional de Ingeniería Mecánica"),
        EdificioCampus(6, "Ingeniería Química y Metalúrgica"),
        EdificioCampus(7, "Facultad de Producción y Servicios"),
        EdificioCampus(8, "Escuela de Matemática y Física"),
        EdificioCampus(9, "Pabellón Nicholson / Pabellón de la Cultura"),
        EdificioCampus(10, "Estadio Ho Chi Minh"),
        EdificioCampus(11, "Infounsa"),
        EdificioCampus(12, "Panificadora e Imprenta Universitaria"),
        EdificioCampus(13, "Ingeniería Geológica"),
        EdificioCampus(14, "Escuela de Arte")
    )
}
