package com.example.navcompose_loginregistro

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// =========================================================================
// 1. FUNCIONES AUXILIARES PARA LA PERSISTENCIA DE DATOS (cuentas.txt)
// =========================================================================

/**
 * Guarda el usuario y la contraseña en un archivo de texto en el almacenamiento interno.
 * Utiliza MODE_APPEND para agregar registros sin sobrescribir los anteriores.
 */
fun guardarCuentaEnArchivo(context: Context, usuario: String, pass: String): Boolean {
    return try {
        val linea = "$usuario:$pass\n"
        context.openFileOutput("cuentas.txt", Context.MODE_APPEND).use { output ->
            output.write(linea.toByteArray())
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * Busca si existe la combinación usuario:contraseña en el archivo cuentas.txt.
 */
fun validarCuentaEnArchivo(context: Context, usuario: String, pass: String): Boolean {
    return try {
        val file = context.getFileStreamPath("cuentas.txt")
        if (!file.exists()) return false

        context.openFileInput("cuentas.txt").bufferedReader().useLines { lines ->
            lines.any { line ->
                val partes = line.split(":")
                partes.size == 2 && partes[0].trim() == usuario.trim() && partes[1].trim() == pass.trim()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

// =========================================================================
// 2. COMPONENTES COMPOSABLE (VISTAS)
// =========================================================================

/**
 * Pantalla de Inicio de Sesión (Login)
 */
@Composable
fun LoginScreen(
    onLoginExitoso: (String) -> Unit,
    onIrARegistro: () -> Unit
) {
    val context = LocalContext.current
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (mensajeError.isNotEmpty()) {
            Text(mensajeError, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                // Validación: Campos vacíos
                if (usuario.isBlank() || password.isBlank()) {
                    mensajeError = "Por favor, ingrese usuario y contraseña."
                } else {
                    // Validación en archivo de texto interno
                    val coincide = validarCuentaEnArchivo(context, usuario, password)
                    if (coincide) {
                        mensajeError = ""
                        onLoginExitoso(usuario)
                    } else {
                        mensajeError = "Cuenta no encontrada"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onIrARegistro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear cuenta")
        }
    }
}

/**
 * Pantalla de Registro de Nuevo Usuario
 */
@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onCancelar: () -> Unit
) {
    val context = LocalContext.current
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Nuevo usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nueva contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (mensajeError.isNotEmpty()) {
            Text(mensajeError, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                // Validación: Campos vacíos
                if (usuario.isBlank() || password.isBlank()) {
                    mensajeError = "Todos los campos son obligatorios."
                } else {
                    // Guardar en cuentas.txt
                    val guardadoCorrecto = guardarCuentaEnArchivo(context, usuario, password)
                    if (guardadoCorrecto) {
                        Toast.makeText(
                            context,
                            "Cuenta creada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        onRegistroExitoso()
                    } else {
                        mensajeError = "Error al guardar la cuenta."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Aceptar")
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}

/**
 * Pantalla de Bienvenida (HomeScreen) que recibe el nombre del usuario
 */
@Composable
fun HomeScreen(
    usuario: String,
    onCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido $usuario",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

// =========================================================================
// 3. ACTIVIDAD PRINCIPAL Y CONFIGURACIÓN DEL NAVHOST
// =========================================================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                // Ruta 1: Pantalla de Login
                composable("login") {
                    LoginScreen(
                        onLoginExitoso = { usuario ->
                            // Navega pasando el parámetro en la ruta
                            navController.navigate("home/$usuario")
                        },
                        onIrARegistro = {
                            navController.navigate("registro")
                        }
                    )
                }

                // Ruta 2: Pantalla de Registro
                composable("registro") {
                    RegistroScreen(
                        onRegistroExitoso = {
                            navController.popBackStack()
                        },
                        onCancelar = {
                            navController.popBackStack()
                        }
                    )
                }

                // Ruta 3: Pantalla de Inicio (Home) con argumento en la URL/route
                composable(
                    route = "home/{usuario}",
                    arguments = listOf(
                        navArgument("usuario") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val usuarioArg = backStackEntry.arguments?.getString("usuario") ?: ""
                    HomeScreen(
                        usuario = usuarioArg,
                        onCerrarSesion = {
                            navController.popBackStack("login", inclusive = false)
                        }
                    )
                }
            }
        }
    }
}