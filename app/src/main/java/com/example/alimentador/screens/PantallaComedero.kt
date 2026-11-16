package com.example.alimentador.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PantallaComedero(navController: NavController, comida: Int, onServir: () -> Unit) {
    var mensaje by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Mi comedero", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Estado del comedero")
        Text("Conectado", color = Color.Green, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Nivel de comida")
        Spacer(modifier = Modifier.height(8.dp))

        // Barra de nivel: una caja gris con otra azul encima
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
                if (comida >= 5) {
                    onServir()
                    mensaje = "Orden enviada al comedero"
                } else {
                    mensaje = "No queda comida"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dispensar ahora")
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
                // Cerrar sesión usando signOut() de FirebaseAuth
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
