package com.example.tarjetascampus_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tarjetascampus_compose.ui.theme.TarjetasCampus_ComposeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ==========================================
// MODELO DE DATOS Y RUTA DE PANTALLAS
// ==========================================
sealed class Screen(val route: String, val label: String) {
    object Home : Screen("home", "Home")
    object Edificios : Screen("edificios", "Edificios")
    object Mapa : Screen("mapa", "Mapa")
    object Perfil : Screen("perfil", "Perfil")
}

data class LugarCampus(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val detalleCompleto: String,
    val colorFondo: Color,
    val categoria: String,
    val imagenResId: Int? = null
)

val listaLugares = listOf(
    LugarCampus(
        id = "1",
        nombre = "Biblioteca Central",
        descripcion = "Zona de estudio y préstamo de libros.",
        detalleCompleto = "La Biblioteca Central ofrece amplias salas de lectura, préstamos de libros físicos y digitales, módulos de trabajo grupal y zona de cómputo.",
        colorFondo = Color(0xFFE3F2FD),
        categoria = "Académico",
        imagenResId = R.drawable.biblioteca
    ),
    LugarCampus(
        id = "2",
        nombre = "Comedor Universitario",
        descripcion = "Servicio de alimentación para estudiantes.",
        detalleCompleto = "El Comedor Universitario brinda raciones balanceadas de alimentos para la comunidad estudiantil con estrictos estándares de calidad.",
        colorFondo = Color(0xFFFFF3E0),
        categoria = "Servicios",
        imagenResId = R.drawable.comedor
    ),
    LugarCampus(
        id = "3",
        nombre = "Pabellón de Sistemas",
        descripcion = "Laboratorios de software y aulas teóricas.",
        detalleCompleto = "Infraestructura equipada con laboratorios de cómputo de última generación y aulas para la Escuela Profesional de Ingeniería de Sistemas.",
        colorFondo = Color(0xFFE8F5E9),
        categoria = "Aulas / Labs",
        imagenResId = R.drawable.pabellon_sistemas
    ),
    LugarCampus(
        id = "4",
        nombre = "Polideportivo Campus",
        descripcion = "Espacio para disciplinas deportivas y eventos.",
        detalleCompleto = "Instalaciones deportivas destinadas al entrenamiento de selecciones universitarias, campeonatos interescuelas y actividades recreativas.",
        colorFondo = Color(0xFFF3E5F5),
        categoria = "Deportes",
        imagenResId = R.drawable.polideportivo
    )
)

// ==========================================
// VIEWMODEL PARA EDIFICIO SELECCIONADO
// ==========================================
class SeleccionViewModel : ViewModel() {
    private val _edificioSeleccionado = MutableStateFlow("Ninguno")
    val edificioSeleccionado: StateFlow<String> = _edificioSeleccionado.asStateFlow()

    fun seleccionarEdificio(nombre: String) {
        _edificioSeleccionado.value = nombre
    }
}

// ==========================================
// ACTIVIDAD PRINCIPAL
// ==========================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TarjetasCampus_ComposeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: SeleccionViewModel = viewModel()) {
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
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(viewModel = viewModel)
            }
            composable(Screen.Edificios.route) {
                EdificiosScreen(
                    onLugarClick = { lugar ->
                        viewModel.seleccionarEdificio(lugar.nombre)
                        navController.navigate("detalle/${lugar.id}")
                    }
                )
            }
            composable(
                route = "detalle/{lugarId}",
                arguments = listOf(navArgument("lugarId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lugarId = backStackEntry.arguments?.getString("lugarId")
                val lugar = listaLugares.find { it.id == lugarId }
                if (lugar != null) {
                    DetalleLugarScreen(
                        lugar = lugar,
                        onVolver = { navController.popBackStack() }
                    )
                }
            }
            composable(Screen.Mapa.route) {
                MapaScreen()
            }
            composable(Screen.Perfil.route) {
                PerfilScreen()
            }
        }
    }
}

// ==========================================
// COMPONENTE DE IMAGEN CON FALLBACK
// ==========================================
@Composable
fun ImagenOColor(imagenResId: Int?, contentDescription: String, modifier: Modifier = Modifier) {
    if (imagenResId != null) {
        Image(
            painter = painterResource(id = imagenResId),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = contentDescription.take(1), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// ESTILOS DE TARJETAS
// ==========================================

// Estilo 1: Tarjeta Horizontal con imagen circular
@Composable
fun TarjetaLugar(
    nombre: String,
    descripcion: String,
    imagenResId: Int?,
    colorFondo: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = colorFondo,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            ImagenOColor(
                imagenResId = imagenResId,
                contentDescription = nombre,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = nombre, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = descripcion, fontSize = 14.sp)
            }
        }
    }
}

// Estilo 2: Tarjeta Apilada con Scrim
@Composable
fun TarjetaCompacta(
    nombre: String,
    imagenResId: Int?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        ImagenOColor(
            imagenResId = imagenResId,
            contentDescription = nombre,
            modifier = Modifier.matchParentSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )
        Text(
            text = nombre,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        )
    }
}

// Estilo 3: Tarjeta Personalizada con Etiqueta (Ejercicio 4)
@Composable
fun TarjetaEtiquetada(
    nombre: String,
    descripcion: String,
    categoria: String,
    imagenResId: Int?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF0F4F8))
            .clickable { onClick() }
    ) {
        Column {
            ImagenOColor(
                imagenResId = imagenResId,
                contentDescription = nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF101828))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = descripcion, fontSize = 13.sp, color = Color.Gray)
            }
        }
        Surface(
            color = Color(0xFF0066CC),
            shape = RoundedCornerShape(bottomEnd = 12.dp),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(
                text = categoria.uppercase(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// ==========================================
// PANTALLAS
// ==========================================

@Composable
fun HomeScreen(viewModel: SeleccionViewModel) {
    val edificioSeleccionado by viewModel.edificioSeleccionado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido a Tarjetas Campus", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Último edificio consultado: $edificioSeleccionado", fontSize = 16.sp)
    }
}

@Composable
fun EdificiosScreen(
    onLugarClick: (LugarCampus) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Edificios del Campus UNSA",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        items(listaLugares) { lugar ->
            when (lugar.id) {
                "1" -> TarjetaLugar(
                    nombre = lugar.nombre,
                    descripcion = lugar.descripcion,
                    imagenResId = lugar.imagenResId,
                    colorFondo = lugar.colorFondo,
                    onClick = { onLugarClick(lugar) }
                )
                "2" -> TarjetaCompacta(
                    nombre = lugar.nombre,
                    imagenResId = lugar.imagenResId,
                    onClick = { onLugarClick(lugar) }
                )
                else -> TarjetaEtiquetada(
                    nombre = lugar.nombre,
                    descripcion = lugar.descripcion,
                    categoria = lugar.categoria,
                    imagenResId = lugar.imagenResId,
                    onClick = { onLugarClick(lugar) }
                )
            }
        }
    }
}

@Composable
fun DetalleLugarScreen(
    lugar: LugarCampus,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = lugar.colorFondo,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImagenOColor(
                    imagenResId = lugar.imagenResId,
                    contentDescription = lugar.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = lugar.nombre, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = lugar.detalleCompleto, fontSize = 16.sp)
            }
        }
        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a Edificios")
        }
    }
}

@Composable
fun MapaScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Mapa de ubicaciones (pendiente de integrar)")
    }
}

@Composable
fun PerfilScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Perfil del Usuario (Marcador de posición)")
    }
}