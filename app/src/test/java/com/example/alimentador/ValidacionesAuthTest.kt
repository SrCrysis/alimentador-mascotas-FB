package com.example.alimentador

import com.example.alimentador.utils.ValidacionesAuth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesAuthTest {

    @Test
    fun correo_formatoValido() {
        assertTrue(ValidacionesAuth.esCorreoValido("usuario@correo.com"))
        assertTrue(ValidacionesAuth.esCorreoValido("test.mascota@dominio.cl"))
        assertTrue(ValidacionesAuth.esCorreoValido("usuario123@sub.dominio.org"))
    }

    @Test
    fun correo_formatoInvalido() {
        assertFalse(ValidacionesAuth.esCorreoValido(""))
        assertFalse(ValidacionesAuth.esCorreoValido("correo_sin_arroba"))
        assertFalse(ValidacionesAuth.esCorreoValido("usuario@"))
        assertFalse(ValidacionesAuth.esCorreoValido("@dominio.com"))
        assertFalse(ValidacionesAuth.esCorreoValido("usuario@dominio"))
    }

    @Test
    fun contrasena_segura_cumpleRequisitos() {
        // Mínimo 8 caracteres, con mayúscula, minúscula y número
        assertTrue(ValidacionesAuth.esContrasenaSegura("Abcdefg1"))
        assertTrue(ValidacionesAuth.esContrasenaSegura("Mascota2026"))
        assertTrue(ValidacionesAuth.esContrasenaSegura("P@ssw0rdSecure"))
    }

    @Test
    fun contrasena_insegura_fallaPorLongitud() {
        // Menos de 8 caracteres
        assertFalse(ValidacionesAuth.esContrasenaSegura("Abc1"))
        assertFalse(ValidacionesAuth.esContrasenaSegura("Pass1"))
    }

    @Test
    fun contrasena_insegura_fallaPorFaltaDeMayuscula() {
        assertFalse(ValidacionesAuth.esContrasenaSegura("abcdefg1"))
    }

    @Test
    fun contrasena_insegura_fallaPorFaltaDeMinuscula() {
        assertFalse(ValidacionesAuth.esContrasenaSegura("ABCDEFG1"))
    }

    @Test
    fun contrasena_insegura_fallaPorFaltaDeNumero() {
        assertFalse(ValidacionesAuth.esContrasenaSegura("Abcdefgh"))
    }

    @Test
    fun validarCredenciales_devuelveErrorApropiado() {
        assertEquals(
            "Correo con formato inválido",
            ValidacionesAuth.validarCredenciales("correoInvalido", "Pass1234")
        )

        assertEquals(
            "Contraseña insegura (mínimo 8 caracteres, con mayúscula, minúscula y número)",
            ValidacionesAuth.validarCredenciales("usuario@test.com", "1234")
        )

        assertNull(
            ValidacionesAuth.validarCredenciales("usuario@test.com", "PasswordSegura1")
        )
    }
}
