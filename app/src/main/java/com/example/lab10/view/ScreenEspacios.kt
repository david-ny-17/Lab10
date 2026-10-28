package com.example.lab10.view

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lab10.data.EspacioApiService
import com.example.lab10.data.EspacioModel
import kotlinx.coroutines.delay

@Composable
fun ContenidoEspaciosListado(navController: NavHostController, servicio: EspacioApiService) {
    val listaEspacios: SnapshotStateList<EspacioModel> = remember { mutableStateListOf() }

    LaunchedEffect(Unit) {
        val listado = servicio.selectEspacios()
        listado.forEach { listaEspacios.add(it) }
    }

    LazyColumn {
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.1f)
                )
                Text(
                    text = "ESPACIO",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.7f)
                )
                Text(
                    text = "Acción",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.2f)
                )
            }
        }

        items(listaEspacios) { item ->
            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.id}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.1f)
                )
                Text(
                    text = item.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.6f)
                )
                IconButton(
                    onClick = {
                        navController.navigate("espacioVer/${item.id}")
                        Log.e("ESPACIO-VER", "ID = ${item.id}")
                    },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                IconButton(
                    onClick = {
                        navController.navigate("espacioDel/${item.id}")
                        Log.e("ESPACIO-DEL", "ID = ${item.id}")
                    },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun ContenidoEspacioEditar(
    navController: NavHostController,
    servicio: EspacioApiService,
    pid: Int = 0
) {
    var id by remember { mutableStateOf(pid) }
    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var aforoMaximo by remember { mutableStateOf("") }
    var estadoOperativo by remember { mutableStateOf("true") }
    var grabar by remember { mutableStateOf(false) }

    if (id != 0) {
        LaunchedEffect(Unit) {
            val objEspacio = servicio.selectEspacio(id.toString())
            delay(100)
            nombre = objEspacio.body()?.nombre ?: ""
            ubicacion = objEspacio.body()?.ubicacion ?: ""
            aforoMaximo = objEspacio.body()?.aforoMaximo?.toString() ?: ""
            estadoOperativo = (objEspacio.body()?.estadoOperativo ?: true).toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = id.toString(),
            onValueChange = { },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true
        )
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre:") },
            singleLine = true
        )
        TextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación:") },
            singleLine = true
        )
        TextField(
            value = aforoMaximo,
            onValueChange = { aforoMaximo = it },
            label = { Text("Aforo Máximo:") },
            singleLine = true
        )
        TextField(
            value = estadoOperativo,
            onValueChange = { estadoOperativo = it },
            label = { Text("Estado Operativo (true/false):") },
            singleLine = true
        )
        Button(
            onClick = {
                grabar = true
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Grabar", fontSize = 16.sp)
        }
    }

    if (grabar) {
        val objEspacio = EspacioModel(
            id = id,
            nombre = nombre,
            ubicacion = ubicacion,
            aforoMaximo = aforoMaximo.toIntOrNull() ?: 0,
            estadoOperativo = estadoOperativo.toBoolean()
        )

        LaunchedEffect(Unit) {
            if (id == 0) {
                servicio.insertEspacio(objEspacio)
            } else {
                servicio.updateEspacio(id.toString(), objEspacio)
            }
            grabar = false
            navController.navigate("espacios")
        }
    }
}

@Composable
fun ContenidoEspacioEliminar(
    navController: NavHostController,
    servicio: EspacioApiService,
    id: Int
) {
    var showDialog by remember { mutableStateOf(true) }
    var borrar by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirmar Eliminación") },
            text = { Text("¿Está seguro de eliminar el Espacio?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        borrar = true
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("espacios")
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (borrar) {
        LaunchedEffect(Unit) {
            servicio.deleteEspacio(id.toString())
            borrar = false
            navController.navigate("espacios")
        }
    }
}