package com.example.listafiltrable_compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListaEdificios() {
    // 1. Colección inmutable de datos preservada en memoria durante recomposiciones
    val edificios = remember {
        listOf(
            Edificio(1, "Biblioteca Central", "Servicios", "Préstamo de libros y salas de estudio"),
            Edificio(2, "Comedor Universitario", "Servicios", "Alimentación para estudiantes"),
            Edificio(3, "Facultad de Ingeniería", "Académico", "Aulas y laboratorios de ingeniería"),
            Edificio(4, "Facultad de Ciencias", "Académico", "Aulas y laboratorios de ciencias"),
            Edificio(5, "Coliseo Universitario", "Deportes", "Actividades deportivas y eventos"),
            Edificio(6, "Auditorio Central", "Eventos", "Conferencias y ceremonias")
        )
    }

    // 2. Estado reactivo mutable para almacenar el criterio de búsqueda ingresado por el usuario
    var texto by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 3. Campo de entrada interactivo ligado mediante delegación de propiedades (by)
        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text("Buscar edificio") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Operación de filtrado dinámico en tiempo real (evaluada en cada recomposición)
        val edificiosFiltrados = edificios.filter {
            it.nombre.contains(texto, ignoreCase = true)
        }

        // 5. Lista perezosa con optimización de claves estables para minimización de recomposiciones
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = edificiosFiltrados,
                key = { it.id } // Clave única por elemento
            ) { edificio ->
                FilaEdificio(edificio = edificio)
            }
        }
    }
}