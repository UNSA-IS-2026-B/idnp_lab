package com.example.helloworldcompose

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegistroLibroScreen()
                }
            }
        }
    }
}

@Composable
fun RegistroLibroScreen() {
    val context = LocalContext.current
    val fileName = "libro_registro.txt"

    // Estados para almacenar los campos de texto
    var titulo by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var paginas by remember { mutableStateOf("") }

    // Estado para mostrar la lectura en pantalla
    var contenidoGuardado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Registro de Libro",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título del libro") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = autor,
            onValueChange = { autor = it },
            label = { Text("Autor") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = paginas,
            onValueChange = { paginas = it },
            label = { Text("Número de páginas leídas") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Botón Guardar
            Button(onClick = {
                val textoAGuardar = "Título: $titulo\nAutor: $autor\nPáginas: $paginas"
                try {
                    context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                        output.write(textoAGuardar.toByteArray())
                    }
                    titulo = ""
                    autor = ""
                    paginas = ""
                } catch (e: Exception) {
                    Log.e("RegistroLibro", "Error al guardar el archivo", e)
                }
            }) {
                Text("Guardar")
            }

            // Botón Ver registro
            Button(onClick = {
                try {
                    val textoLeido = context.openFileInput(fileName).bufferedReader().use { it.readText() }

                    // Imprimir en consola con Log.d
                    Log.d("RegistroLibro", "Contenido del archivo:\n$textoLeido")

                    // Actualizar variable de estado para la pantalla
                    contenidoGuardado = textoLeido
                } catch (e: Exception) {
                    Log.e("RegistroLibro", "Error al leer el archivo", e)
                    contenidoGuardado = "No hay datos guardados aún."
                }
            }) {
                Text("Ver registro")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra el registro leído en pantalla
        if (contenidoGuardado.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Registro guardado:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = contenidoGuardado)
                }
            }
        }
    }
}