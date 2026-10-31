package com.example.persistenciaroom_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.persistenciaroom_compose.ui.theme.PersistenciaRoom_ComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = crearBaseDeDatos(applicationContext)
        val dao = database.edificioDao()
        val factory = CampusViewModelFactory(dao)

        setContent {
            PersistenciaRoom_ComposeTheme {
                val viewModel: CampusViewModel = viewModel(factory = factory)
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaCampus(viewModel)
                }
            }
        }
    }
}