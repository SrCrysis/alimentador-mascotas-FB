package com.example.alimentador.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alimentador.Entrega
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun PantallaRegistro(navController: NavController) {
    val historial = remember { mutableStateListOf<Entrega>() }

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "sin_usuario"
    val referencia = FirebaseDatabase.getInstance().getReference("usuarios/$uid/comedero/entregas")

    DisposableEffect(uid) {
        val escuchador = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historial.clear()
                for (hijo in snapshot.children) {
                    val entrega = hijo.getValue(Entrega::class.java)
                    if (entrega != null) {
                        historial.add(0, entrega)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        referencia.addValueEventListener(escuchador)
        onDispose {
            referencia.removeEventListener(escuchador)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Historial de entregas", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(historial) { entrega ->
                Text(
                    text = "Entrega ${entrega.numero} - ${entrega.origen}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(12.dp)
                )
            }
        }
    }
}
