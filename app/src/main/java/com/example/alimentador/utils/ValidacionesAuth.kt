package com.example.alimentador.utils

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

object ValidacionesAuth {

    fun esCorreoValido(correo: String): Boolean {
        val trimmed = correo.trim()
        if (trimmed.isEmpty()) return false
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return regex.matches(trimmed)
    }

    fun esContrasenaSegura(contrasena: String): Boolean {
        if (contrasena.length < 8) return false
        val tieneMayuscula = contrasena.any { it.isUpperCase() }
        val tieneMinuscula = contrasena.any { it.isLowerCase() }
        val tieneNumero = contrasena.any { it.isDigit() }
        return tieneMayuscula && tieneMinuscula && tieneNumero
    }

    fun validarCredenciales(correo: String, contrasena: String): String? {
        if (correo.isBlank() || !esCorreoValido(correo)) {
            return "Correo con formato inválido"
        }
        if (contrasena.isBlank() || !esContrasenaSegura(contrasena)) {
            return "Contraseña insegura (mínimo 8 caracteres, con mayúscula, minúscula y número)"
        }
        return null
    }

    fun parsearErrorFirebase(exception: Throwable?): String {
        if (exception == null) return "Error de autenticación desconocido"

        val codigo = (exception as? FirebaseAuthException)?.errorCode.orEmpty()
        val mensaje = exception.message.orEmpty()

        return when {
            (exception is FirebaseAuthUserCollisionException) || (codigo == "ERROR_EMAIL_ALREADY_IN_USE") -> {
                "El correo ya se encuentra registrado"
            }
            (exception is FirebaseAuthInvalidUserException) || (codigo == "ERROR_USER_NOT_FOUND") -> {
                "Usuario inexistente"
            }
            (exception is FirebaseAuthWeakPasswordException) || (codigo == "ERROR_WEAK_PASSWORD") -> {
                "Contraseña insegura (mínimo 8 caracteres, con mayúscula, minúscula y número)"
            }
            (exception is FirebaseAuthInvalidCredentialsException) ||
                    (codigo == "ERROR_WRONG_PASSWORD") ||
                    (codigo == "ERROR_INVALID_CREDENTIAL") -> {
                if (codigo == "ERROR_WRONG_PASSWORD") {
                    "Contraseña incorrecta"
                } else {
                    "Contraseña incorrecta o usuario inexistente"
                }
            }
            codigo == "ERROR_INVALID_EMAIL" -> {
                "Correo con formato inválido"
            }
            mensaje.contains("already in use", ignoreCase = true) -> {
                "El correo ya se encuentra registrado"
            }
            mensaje.contains("password", ignoreCase = true) -> {
                "Contraseña incorrecta"
            }
            mensaje.contains("no user record", ignoreCase = true) || mensaje.contains("user not found", ignoreCase = true) -> {
                "Usuario inexistente"
            }
            else -> {
                exception.localizedMessage ?: "Error de autenticación"
            }
        }
    }
}
