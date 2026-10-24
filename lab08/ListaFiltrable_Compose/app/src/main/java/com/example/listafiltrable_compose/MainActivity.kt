/**

package com.example.listafiltrable_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.listafiltrable_compose.ui.theme.ListaFiltrable_ComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListaFiltrable_ComposeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ListaEdificios()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListaEdificiosPreview() {
    ListaFiltrable_ComposeTheme {
        ListaEdificios()
    }
}
 */

package com.example.listafiltrable_compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.listafiltrable_compose.propuestos.EdificiosScreen
import com.example.listafiltrable_compose.ui.theme.ListaFiltrable_ComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListaFiltrable_ComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

// Ejecución de la vista propuesta con filtrado y persistencia
                    EdificiosScreen(
                        onLugarClick = { lugar ->
                            Toast.makeText(
                                context,
                                "Seleccionado: ${lugar.nombre}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EdificiosScreenPreview() {
    ListaFiltrable_ComposeTheme {
        EdificiosScreen()
    }
}

