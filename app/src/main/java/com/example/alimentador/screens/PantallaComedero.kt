package com.example.alimentador.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alimentador.ConexionBT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun PantallaComedero(navController: NavController) {
    var comida by remember { mutableStateOf(0) }
    var estado by remember { mutableStateOf("Desconectado") }
    var mensaje by remember { mutableStateOf("") }

    val contexto = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "sin_usuario"
    val referencia = FirebaseDatabase.getInstance().getReference("usuarios/$uid/comedero")

    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            ConexionBT.conectar(contexto, uid) { texto -> mensaje = texto }
        } else {
            mensaje = "Sin permiso de Bluetooth"
        }
    }

    DisposableEffect(uid) {
        val escuchador = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nivel = snapshot.child("nivel").getValue(Int::class.java)
                if (nivel == null) {
                    referencia.child("nivel").setValue(100)
                } else {
                    comida = nivel
                }
                estado = snapshot.child("estado").getValue(String::class.java) ?: "Desconectado"
            }

            override fun onCancelled(error: DatabaseError) {
                mensaje = "No se pudo leer la base de datos"
            }
        }
        referencia.addValueEventListener(escuchador)
        onDispose {
            referencia.removeEventListener(escuchador)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Mi comedero", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Estado del comedero")
        if (estado == "Conectado") {
            Text(estado, color = Color.Green, fontSize = 20.sp)
        } else {
            Text(estado, color = Color.Red, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text("Nivel de comida")
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(Color.LightGray)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(comida / 100f)
                    .height(16.dp)
                    .background(Color.Blue)
            )
        }
        Text("$comida%", fontSize = 20.sp)

        if (comida < 20) {
            Text("Comida baja, rellena el comedero", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (estado == "Conectado") {
                    referencia.child("orden").setValue(true)
                    mensaje = "Orden enviada al comedero"
                } else {
                    mensaje = "El comedero está desconectado"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dispensar ahora")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= 31) {
                    pedirPermiso.launch(Manifest.permission.BLUETOOTH_CONNECT)
                } else {
                    ConexionBT.conectar(contexto, uid) { texto -> mensaje = texto }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Conectar con el comedero")
        }

        Button(
            onClick = { ConexionBT.simular(uid) { texto -> mensaje = texto } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simular comedero (prueba)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                referencia.child("nivel").setValue(100)
                mensaje = "Comedero rellenado"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ya rellené el comedero")
        }

        Text(mensaje)

        Button(
            onClick = { navController.navigate("registro") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver historial")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("ingreso") {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
