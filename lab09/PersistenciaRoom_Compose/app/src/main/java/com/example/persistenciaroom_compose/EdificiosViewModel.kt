package com.example.persistenciaroom_compose
/*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EdificiosViewModel(private val dao: EdificioDao) : ViewModel() {

    private val _edificios = MutableStateFlow<List<EdificioConAmbientes>>(emptyList())
    val edificios: StateFlow<List<EdificioConAmbientes>> = _edificios

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (dao.obtenerTodos().isEmpty()) {
                val id1 = dao.insertarEdificio(
                    EdificioEntity(nombre = "Biblioteca Central", categoria = "Servicios")
                )
                dao.insertarAmbiente(AmbienteEntity(edificioId = id1.toInt(), nombre = "Sala de lectura"))
                dao.insertarAmbiente(AmbienteEntity(edificioId = id1.toInt(), nombre = "Hemeroteca"))

                val id2 = dao.insertarEdificio(
                    EdificioEntity(nombre = "Facultad de Ingeniería", categoria = "Académico")
                )
                dao.insertarAmbiente(
                    AmbienteEntity(edificioId = id2.toInt(), nombre = "Laboratorio de cómputo")
                )
            }
            _edificios.value = dao.obtenerConAmbientes()
        }
    }
}

class EdificiosViewModelFactory(private val dao: EdificioDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EdificiosViewModel(dao) as T
    }
}
*/
