package com.example.persistenciaroom_compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaCampus(viewModel: CampusViewModel) {
    val listaEdificiosConAmbientes by viewModel.edificiosConAmbientes.collectAsState()
    val listaAmbientesConNotas by viewModel.ambientesConNotas.collectAsState()

    var edificioAEditar by remember { mutableStateOf<EdificioEntity?>(null) }
    var ambienteAEditar by remember { mutableStateOf<AmbienteEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Gestión de Campus (Persistencia Room)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listaEdificiosConAmbientes, key = { it.edificio.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.edificio.nombre,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Categoría: ${item.edificio.categoria}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Row {
                                Button(
                                    onClick = { edificioAEditar = item.edificio },
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Text("Editar", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { viewModel.eliminarEdificio(item.edificio) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Eliminar", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ambientes y Notas Relacionadas:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        item.ambientes.forEach { ambiente ->
                            val notasDelAmbiente = listaAmbientesConNotas
                                .find { it.ambiente.id == ambiente.id }
                                ?.notas ?: emptyList()

                            Column(
                                modifier = Modifier
                                    .padding(start = 8.dp, top = 6.dp)
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "• ${ambiente.nombre}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Row {
                                        TextButton(onClick = { ambienteAEditar = ambiente }) {
                                            Text("Editar", fontSize = 11.sp)
                                        }
                                        TextButton(
                                            onClick = { viewModel.eliminarAmbiente(ambiente) }
                                        ) {
                                            Text(
                                                "Eliminar",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                notasDelAmbiente.forEach { nota ->
                                    Text(
                                        text = "   └ Nota: ${nota.texto}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    edificioAEditar?.let { edificio ->
        var nombreTexto by remember { mutableStateOf(edificio.nombre) }
        var categoriaTexto by remember { mutableStateOf(edificio.categoria) }

        AlertDialog(
            onDismissRequest = { edificioAEditar = null },
            title = { Text("Actualizar Edificio") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nombreTexto,
                        onValueChange = { nombreTexto = it },
                        label = { Text("Nombre del Edificio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = categoriaTexto,
                        onValueChange = { categoriaTexto = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.actualizarEdificio(edificio, nombreTexto, categoriaTexto)
                    edificioAEditar = null
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { edificioAEditar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    ambienteAEditar?.let { ambiente ->
        var nombreTexto by remember { mutableStateOf(ambiente.nombre) }

        AlertDialog(
            onDismissRequest = { ambienteAEditar = null },
            title = { Text("Actualizar Ambiente") },
            text = {
                OutlinedTextField(
                    value = nombreTexto,
                    onValueChange = { nombreTexto = it },
                    label = { Text("Nombre del Ambiente") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.actualizarAmbiente(ambiente, nombreTexto)
                    ambienteAEditar = null
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { ambienteAEditar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}