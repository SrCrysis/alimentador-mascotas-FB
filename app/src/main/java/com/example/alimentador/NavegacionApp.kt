package com.example.alimentador

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alimentador.screens.PantallaComedero
import com.example.alimentador.screens.PantallaCrearCuenta
import com.example.alimentador.screens.PantallaIngreso
import com.example.alimentador.screens.PantallaRegistro
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavegacionApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val auth = remember { FirebaseAuth.getInstance() }

    // Persistencia de sesión: si el usuario ya inició sesión, ingresa directo a "comedero"
    val destinoInicial = remember {
        if (auth.currentUser != null) "comedero" else "ingreso"
    }

    // Datos compartidos entre las pantallas
    var comida by remember { mutableIntStateOf(75) }
    val historial = remember {
        mutableStateListOf(
            Entrega(3, "Automática"),
            Entrega(2, "Manual"),
            Entrega(1, "Automática"),
        )
    }

    NavHost(
        navController = navController,
        startDestination = destinoInicial,
        modifier = modifier
    ) {
        composable("ingreso") {
            PantallaIngreso(navController)
        }
        composable("crear_cuenta") {
            PantallaCrearCuenta(navController)
        }
        composable("comedero") {
            PantallaComedero(
                navController = navController,
                comida = comida,
                onServir = {
                    comida -= 5
                    historial.add(0, Entrega(historial.size + 1, "Manual"))
                }
            )
        }
        composable("registro") {
            PantallaRegistro(navController, historial)
        }
        composable("historial") {
            PantallaRegistro(navController, historial)
        }
    }
}
