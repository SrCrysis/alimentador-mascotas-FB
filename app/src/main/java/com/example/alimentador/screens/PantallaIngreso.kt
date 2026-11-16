package com.example.alimentador.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alimentador.utils.ValidacionesAuth
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PantallaIngreso(navController: NavController) {
    var correo by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    val auth = remember { FirebaseAuth.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Alimentador de Mascotas", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = correo,
            onValueChange = {
                correo = it
                errorMensaje = null
            },
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = pass,
            onValueChange = {
                pass = it
                errorMensaje = null
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Error visible en color rojo ante validaciones fallidas o errores de Firebase
        if (!errorMensaje.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMensaje ?: "",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (cargando) {
            CircularProgressIndicator(modifier = Modifier.size(36.dp))
        } else {
            Button(
                onClick = {
                    // Validaciones locales en la app ANTES de llamar a Firebase
                    if (correo.isBlank() || !ValidacionesAuth.esCorreoValido(correo)) {
                        errorMensaje = "Correo con formato inválido"
                        return@Button
                    }
                    if (pass.isBlank() || !ValidacionesAuth.esContrasenaSegura(pass)) {
                        errorMensaje = "Contraseña insegura (mínimo 8 caracteres, con mayúscula, minúscula y número)"
                        return@Button
                    }

                    // Inicio de sesión con FirebaseAuth y cifrado seguro de credenciales
                    cargando = true
                    errorMensaje = null

                    auth.signInWithEmailAndPassword(correo.trim(), pass)
                        .addOnSuccessListener {
                            cargando = false
                            navController.navigate("comedero") {
                                popUpTo("ingreso") { inclusive = true }
                            }
                        }
                        .addOnFailureListener { exception ->
                            cargando = false
                            errorMensaje = ValidacionesAuth.parsearErrorFirebase(exception)
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    navController.navigate("crear_cuenta")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta")
            }
        }
    }
}
