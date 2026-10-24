package com.example.listafiltrable_compose.propuestos

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Modelo de datos para las entidades del Campus UNSA
data class LugarCampus(
    val id: String,
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    val detalleCompleto: String,
    val icono: ImageVector,
    val colorFondo: Color
)

/**
 * Mapea la clave textual del archivo 'edificios.txt' a un ImageVector de Material Icons.
 */
fun obtenerIconoPorClave(clave: String): ImageVector {
    return when (clave.trim()) {
        "Computer" -> Icons.Default.Computer
        "MenuBook" -> Icons.Default.MenuBook
        "Architecture" -> Icons.Default.Architecture
        "Engineering" -> Icons.Default.Engineering
        "Build" -> Icons.Default.Build
        "Science" -> Icons.Default.Science
        "Business" -> Icons.Default.Business
        "Functions" -> Icons.Default.Functions
        "AccountBalance" -> Icons.Default.AccountBalance
        "SportsSoccer" -> Icons.Default.SportsSoccer
        "Dns" -> Icons.Default.Dns
        "Store" -> Icons.Default.Store
        "Terrain" -> Icons.Default.Terrain
        "Palette" -> Icons.Default.Palette
        else -> Icons.Default.Place
    }
}

/**
 * Asigna paletas cromáticas suaves de fondo según la categoría del edificio.
 */
fun obtenerColorPorCategoria(categoria: String): Color {
    return when (categoria.trim()) {
        "Aulas / Labs" -> Color(0xFFE8F5E9)
        "Académico" -> Color(0xFFE3F2FD)
        "Servicios" -> Color(0xFFFFF3E0)
        "Deportes" -> Color(0xFFF3E5F5)
        "Cultura" -> Color(0xFFFFFDE7)
        else -> Color(0xFFF5F5F5)
    }
}

/**
 * Lee los datos reales de 'edificios.txt'. Si no existe en la memoria interna,
 * crea el archivo con la lista completa de infraestructuras del Campus Ingenierías.
 */
fun cargarEdificiosDesdeArchivo(context: Context): List<LugarCampus> {
    val fileName = "edificios.txt"
    val file = context.getFileStreamPath(fileName)

    // Crear archivo por defecto si no se encuentra registrado en el almacenamiento interno
    if (!file.exists()) {
        val contenidoInicial = """
            1|Escuela Profesional de Ingeniería de Sistemas|Aulas / Labs|Laboratorios de software, inteligencia artificial y redes.|Pabellón especializado equipado con laboratorios de cómputo de última generación, instalaciones de redes y centros de investigación de la EPIS.|Computer
            2|Biblioteca de Ingenierías|Académico|Salas de estudio, bibliografía técnica y bases digitales.|Ofrece amplias salas de lectura individual y grupal, acceso a bases de datos científicas e información especializada en todas las ramas de la ingeniería.|MenuBook
            3|Facultad de Arquitectura|Académico|Talleres de diseño, maquetación y planificación urbana.|Infraestructura con talleres de modelado, diseño bioclimático, aulas teóricas, laboratorios de dibujo y centros de maquetación.|Architecture
            4|Escuela Profesional de Ingeniería Civil|Aulas / Labs|Laboratorios de Estructuras, Suelos y Topografía.|Cuenta con laboratorios de mecánica de suelos, ensayo de materiales de construcción, hidráulica y gabinetes de topografía.|Engineering
            5|Escuela Profesional de Ingeniería Mecánica|Aulas / Labs|Talleres de manufactura, procesos térmicos y motores.|Instalaciones equipadas con talleres de soldadura, procesamiento de metales, diseño asistido por computadora (CAD/CAM) y mecánica pesada.|Build
            6|Ingeniería Química y Metalúrgica|Aulas / Labs|Laboratorios de procesos químicos, pirometallurgia y análisis.|Pabellones dedicados al estudio de transformaciones químicas, procesamiento de minerales, balances de materia y ensayos de materiales.|Science
            7|Facultad de Producción y Servicios|Académico|Decanato, aulas de posgrado y oficinas administrativas.|Centro administrativo principal de la FIPS que alberga decanatos, coordinaciones académicas, salas de docentes y auditorios principal y secundario.|Business
            8|Escuela de Matemática y Física|Académico|Aulas teóricas, laboratorios de física básica y óptica.|Infraestructura para la enseñanza de ciencias puras, cálculo aplicado, física experimental, óptica y laboratorios de investigación teórica.|Functions
            9|Pabellón Nicholson / Pabellón de la Cultura|Cultura|Edificio histórico, aula magna y eventos culturales.|Pabellón de alto valor arquitectónico patrimonial que acoge conferencias magistrales, actos protocolarios, congresos y galerías de la comunidad unsaína.|AccountBalance
            10|Estadio Ho Chi Minh|Deportes|Cancha de fútbol, pista atlética y tribunas.|Escenario deportivo emblemático del campus para torneos interescuelas, competencias atléticas universitarias y entrenamiento de selecciones.|SportsSoccer
            11|Infounsa|Servicios|Centro de informática, redes y soporte tecnológico.|Oficinas centrales de tecnologías de información, administración de la infraestructura de red universitaria, servidores y capacitación digital.|Dns
            12|Panificadora e Imprenta Universitaria|Servicios|Unidades de producción de alimentos e impresiones.|Centros de producción de la universidad encargados del abastecimiento de panadería para el comedor y servicios de imprenta institucional y académica.|Store
            13|Ingeniería Geológica|Aulas / Labs|Laboratorio de petrología, mineralogía y geotecnia.|Laboratorios especializados en el estudio de muestras rocosas, mapas geológicos, prospección minera y mecánica de rocas.|Terrain
            14|Escuela de Arte|Cultura|Talleres de escultura, pintura y artes plásticas.|Espacios creativos equipados para el desarrollo de disciplinas artísticas, exposiciones universitarias y talleres abiertos a la comunidad.|Palette
        """.trimIndent()

        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(contenidoInicial.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Lectura y parseo estricto del archivo
    val lista = mutableListOf<LugarCampus>()
    try {
        context.openFileInput(fileName).bufferedReader().useLines { lines ->
            lines.forEach { line ->
                if (line.isNotBlank()) {
                    val partes = line.split("|")
                    if (partes.size >= 6) {
                        lista.add(
                            LugarCampus(
                                id = partes[0].trim(),
                                nombre = partes[1].trim(),
                                categoria = partes[2].trim(),
                                descripcion = partes[3].trim(),
                                detalleCompleto = partes[4].trim(),
                                icono = obtenerIconoPorClave(partes[5]),
                                colorFondo = obtenerColorPorCategoria(partes[2].trim())
                            )
                        )
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return lista
}