package com.example.persistenciaroom_compose
/*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListaEdificiosConAmbientes(viewModel: EdificiosViewModel) {
    val edificios by viewModel.edificios.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(edificios, key = { it.edificio.id }) { item ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = item.edificio.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                item.ambientes.forEach { ambiente ->
                    Text(text = "- ${ambiente.nombre}", fontSize = 14.sp)
                }
            }
        }
    }
}
 */