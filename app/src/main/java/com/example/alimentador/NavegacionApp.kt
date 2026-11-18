package com.example.alimentador

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

    val destinoInicial = remember {
        if (auth.currentUser != null) "comedero" else "ingreso"
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
            PantallaComedero(navController)
        }
        composable("registro") {
            PantallaRegistro(navController)
        }
    }
}
