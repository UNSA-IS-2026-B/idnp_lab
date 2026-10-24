package com.example.listafiltrable_compose.propuestos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Estilo 1: Tarjeta Horizontal con contenedor circular de ícono (Lab 06)
@Composable
fun TarjetaLugar(
    lugar: LugarCampus,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = lugar.colorFondo,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = lugar.icono,
                    contentDescription = lugar.nombre,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = lugar.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1D1B20)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = lugar.descripcion,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

// Estilo 2: Tarjeta Apilada Compacta (Lab 06)
@Composable
fun TarjetaCompacta(
    lugar: LugarCampus,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = lugar.colorFondo),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = lugar.icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = lugar.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = lugar.descripcion,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// Estilo 3: Tarjeta Personalizada con Etiqueta Flotante de Categoría (Lab 06)
@Composable
fun TarjetaEtiquetada(
    lugar: LugarCampus,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(lugar.colorFondo)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = lugar.icono,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lugar.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = lugar.descripcion,
                fontSize = 13.sp,
                color = Color.DarkGray
            )
        }

        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(bottomEnd = 10.dp),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(
                text = lugar.categoria.uppercase(),
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}