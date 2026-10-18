package com.example.planointeractivo_compose

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.planointeractivo_compose.ui.theme.PlanoInteractivo_ComposeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray

// ==========================================
// MODELO Y LECTURA DE DATOS (Ejercicio 2)
// ==========================================
data class Marcador(
    val offset: Offset,
    val radio: Float,
    val titulo: String,
    val descripcion: String
)

fun cargarMarcadoresDesdeAssets(context: Context): List<Marcador> {
    val lista = mutableListOf<Marcador>()
    try {
        val jsonString = context.assets.open("marcadores.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val x = obj.getDouble("x").toFloat()
            val y = obj.getDouble("y").toFloat()
            val radio = obj.getDouble("radio").toFloat()
            val titulo = obj.getString("titulo")
            val descripcion = obj.getString("descripcion")
            lista.add(Marcador(Offset(x, y), radio, titulo, descripcion))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return lista
}

// ==========================================
// VIEWMODEL (Ejercicio 3)
// ==========================================
class PlanoViewModel : ViewModel() {
    private val _marcadores = MutableStateFlow<List<Marcador>>(emptyList())
    val marcadores: StateFlow<List<Marcador>> = _marcadores.asStateFlow()

    private val _marcadorSeleccionado = MutableStateFlow<Marcador?>(null)
    val marcadorSeleccionado: StateFlow<Marcador?> = _marcadorSeleccionado.asStateFlow()

    fun inicializarMarcadores(context: Context) {
        if (_marcadores.value.isEmpty()) {
            _marcadores.value = cargarMarcadoresDesdeAssets(context)
        }
    }

    fun seleccionarMarcador(marcador: Marcador?) {
        _marcadorSeleccionado.value = marcador
    }
}

// ==========================================
// RUTA DE PANTALLAS (Ejercicio 4)
// ==========================================
sealed class Screen(val route: String, val label: String) {
    object Home : Screen("home", "Home")
    object Edificios : Screen("edificios", "Edificios")
    object Mapa : Screen("mapa", "Mapa")
    object Perfil : Screen("perfil", "Perfil")
}

// ==========================================
// COMPOSABLE PLANO AMBIENTE (Ejercicios 1 y 3)
// ==========================================
@Composable
fun PlanoAmbiente(planoViewModel: PlanoViewModel = viewModel()) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        planoViewModel.inicializarMarcadores(context)
    }

    val marcadores by planoViewModel.marcadores.collectAsState()
    val marcadorSeleccionado by planoViewModel.marcadorSeleccionado.collectAsState()
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Plano del Pabellón de Sistemas (UNSA)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val tocado = marcadores.find { marcador ->
                            (offset - marcador.offset).getDistance() <= marcador.radio
                        }
                        planoViewModel.seleccionarMarcador(tocado)
                    }
                }
        ) {
            // Ambiente 1: Lab LIS
            drawRect(
                color = Color(0xFFBBDEFB),
                topLeft = Offset(20f, 20f),
                size = Size(160f, 130f)
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "Lab LIS",
                topLeft = Offset(30f, 30f),
                style = TextStyle(color = Color(0xFF0D47A1), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            )

            // Ambiente 2: Lab LIDS
            drawRect(
                color = Color(0xFFC8E6C9),
                topLeft = Offset(200f, 20f),
                size = Size(160f, 130f)
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "Lab LIDS",
                topLeft = Offset(210f, 30f),
                style = TextStyle(color = Color(0xFF1B5E20), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            )

            // Ambiente 3: Aula 101
            drawRect(
                color = Color(0xFFFFECB3),
                topLeft = Offset(20f, 170f),
                size = Size(160f, 130f)
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "Aula 101",
                topLeft = Offset(30f, 180f),
                style = TextStyle(color = Color(0xFFE65100), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            )

            // Ambiente 4: Patio Central
            drawRect(
                color = Color(0xFFE1BEE7),
                topLeft = Offset(200f, 170f),
                size = Size(160f, 130f)
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "Patio Central",
                topLeft = Offset(210f, 180f),
                style = TextStyle(color = Color(0xFF4A148C), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            )

            // Paredes divisoras y pasillos
            drawLine(
                color = Color.DarkGray,
                start = Offset(188f, 10f),
                end = Offset(188f, 310f),
                strokeWidth = 6f
            )
            drawLine(
                color = Color.DarkGray,
                start = Offset(10f, 158f),
                end = Offset(370f, 158f),
                strokeWidth = 6f
            )

            // Detección y dibujo de marcadores interactivos
            marcadores.forEach { marcador ->
                drawCircle(
                    color = Color(0xFFD32F2F),
                    radius = marcador.radio,
                    center = marcador.offset
                )
            }
        }
    }

    // Modal AlertDialog informativo
    marcadorSeleccionado?.let { marcador ->
        AlertDialog(
            onDismissRequest = { planoViewModel.seleccionarMarcador(null) },
            title = { Text(marcador.titulo, fontWeight = FontWeight.Bold) },
            text = { Text(marcador.descripcion) },
            confirmButton = {
                TextButton(onClick = { planoViewModel.seleccionarMarcador(null) }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

// ==========================================
// ACTIVIDAD Y NAVEGACIÓN PRINCIPAL
// ==========================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlanoInteractivo_ComposeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Edificios, Screen.Mapa, Screen.Perfil)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    val icon = when (screen) {
                        Screen.Home -> Icons.Default.Home
                        Screen.Edificios -> Icons.Default.List
                        Screen.Mapa -> Icons.Default.Place
                        Screen.Perfil -> Icons.Default.Person
                    }
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = { navController.navigate(screen.route) },
                        icon = { Icon(icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Mapa.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) { Text("Pantalla de Inicio", modifier = Modifier.padding(16.dp)) }
            composable(Screen.Edificios.route) { Text("Lista de Edificios", modifier = Modifier.padding(16.dp)) }
            composable(Screen.Mapa.route) { PlanoAmbiente() }
            composable(Screen.Perfil.route) { Text("Perfil de Usuario", modifier = Modifier.padding(16.dp)) }
        }
    }
}