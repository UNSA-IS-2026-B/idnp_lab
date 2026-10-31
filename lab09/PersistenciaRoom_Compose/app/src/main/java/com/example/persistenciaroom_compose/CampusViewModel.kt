package com.example.persistenciaroom_compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CampusViewModel(private val dao: EdificioDao) : ViewModel() {

    private val _edificiosConAmbientes = MutableStateFlow<List<EdificioConAmbientes>>(emptyList())
    val edificiosConAmbientes: StateFlow<List<EdificioConAmbientes>> = _edificiosConAmbientes

    private val _ambientesConNotas = MutableStateFlow<List<AmbienteConNotas>>(emptyList())
    val ambientesConNotas: StateFlow<List<AmbienteConNotas>> = _ambientesConNotas

    init {
        cargarDatosIniciales()
    }

    fun cargarDatos() {
        viewModelScope.launch(Dispatchers.IO) {
            _edificiosConAmbientes.value = dao.obtenerEdificiosConAmbientes()
            _ambientesConNotas.value = dao.obtenerAmbientesConNotas()
        }
    }

    private fun cargarDatosIniciales() {
        viewModelScope.launch(Dispatchers.IO) {
            if (dao.obtenerTodosLosEdificios().isEmpty()) {
                val ed1 = dao.insertarEdificio(
                    EdificioEntity(nombre = "Pabellón A - Sistemas", categoria = "Académico")
                )
                val amb1 = dao.insertarAmbiente(
                    AmbienteEntity(edificioId = ed1.toInt(), nombre = "Laboratorio de Cómputo 1")
                )
                dao.insertarNota(NotaEntity(ambienteId = amb1.toInt(), texto = "Proyector operativo - 30 PCs"))
                dao.insertarNota(NotaEntity(ambienteId = amb1.toInt(), texto = "Mantenimiento programado sábado"))

                val ed2 = dao.insertarEdificio(
                    EdificioEntity(nombre = "Biblioteca Central", categoria = "Servicios")
                )
                val amb2 = dao.insertarAmbiente(
                    AmbienteEntity(edificioId = ed2.toInt(), nombre = "Sala de Investigación")
                )
                dao.insertarNota(NotaEntity(ambienteId = amb2.toInt(), texto = "Aforo máximo: 40 personas"))
            }
            cargarDatos()
        }
    }

    // --- Operaciones CRUD: Edificio ---
    fun actualizarEdificio(edificio: EdificioEntity, nuevoNombre: String, nuevaCategoria: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.actualizarEdificio(edificio.copy(nombre = nuevoNombre, categoria = nuevaCategoria))
            cargarDatos()
        }
    }

    fun eliminarEdificio(edificio: EdificioEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.eliminarEdificio(edificio)
            cargarDatos()
        }
    }

    // --- Operaciones CRUD: Ambiente ---
    fun actualizarAmbiente(ambiente: AmbienteEntity, nuevoNombre: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.actualizarAmbiente(ambiente.copy(nombre = nuevoNombre))
            cargarDatos()
        }
    }

    fun eliminarAmbiente(ambiente: AmbienteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.eliminarAmbiente(ambiente)
            cargarDatos()
        }
    }
}

class CampusViewModelFactory(private val dao: EdificioDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CampusViewModel(dao) as T
    }
}