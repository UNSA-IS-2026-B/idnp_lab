package com.example.listafiltrable_compose.propuestos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdificiosScreen(
    onLugarClick: (LugarCampus) -> Unit = {}
) {
    val context = LocalContext.current

    // Carga de datos reales desde 'edificios.txt'
    val listaCompleta = remember { cargarEdificiosDesdeArchivo(context) }

    // Estados reactivos de la interfaz
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var menuExpandido by remember { mutableStateOf(false) }

    // Lista de categorías únicas para el menú desplegable
    val categoriasDisponibles = remember(listaCompleta) {
        listOf("Todas") + listaCompleta.map { it.categoria }.distinct()
    }

    // Filtrado multicriterio recalculado eficientemente
    val edificiosFiltrados = remember(textoBusqueda, categoriaSeleccionada, listaCompleta) {
        listaCompleta.filter { edificio ->
            val coincideTexto = edificio.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    edificio.descripcion.contains(textoBusqueda, ignoreCase = true)
            val coincideCategoria = categoriaSeleccionada == "Todas" ||
                    edificio.categoria.equals(categoriaSeleccionada, ignoreCase = true)

            coincideTexto && coincideCategoria
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Campus UNSA - Ingenierías",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filtro 1: Campo de texto
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            label = { Text("Buscar por nombre o descripción") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (textoBusqueda.isNotEmpty()) {
                    IconButton(onClick = { textoBusqueda = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filtro 2: Menú Desplegable por Categoría
        ExposedDropdownMenuBox(
            expanded = menuExpandido,
            onExpandedChange = { menuExpandido = !menuExpandido },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "Categoría: $categoriaSeleccionada",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = menuExpandido,
                onDismissRequest = { menuExpandido = false }
            ) {
                categoriasDisponibles.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            categoriaSeleccionada = cat
                            menuExpandido = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Contador de registros filtrados
        Text(
            text = "Mostrando ${edificiosFiltrados.size} de ${listaCompleta.size} ubicaciones en el campus",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Manejo de estado sin coincidencias
        if (edificiosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No se encontraron edificios con el criterio ingresado",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            // Renderizado perezoso optimizado mediante LazyColumn
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = edificiosFiltrados,
                    key = { it.id }
                ) { lugar ->
                    when (lugar.categoria) {
                        "Aulas / Labs" -> TarjetaLugar(lugar = lugar, onClick = { onLugarClick(lugar) })
                        "Servicios" -> TarjetaCompacta(lugar = lugar, onClick = { onLugarClick(lugar) })
                        else -> TarjetaEtiquetada(lugar = lugar, onClick = { onLugarClick(lugar) })
                    }
                }
            }
        }
    }
}