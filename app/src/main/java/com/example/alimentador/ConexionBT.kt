package com.example.alimentador

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

object ConexionBT {
    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var socket: BluetoothSocket? = null
    private var totalEntregas = 0

    @SuppressLint("MissingPermission")
    fun conectar(context: Context, uid: String, avisar: (String) -> Unit) {
        val manager = context.getSystemService(BluetoothManager::class.java)
        val adaptador = manager?.adapter
        if (adaptador == null) {
            avisar("Este equipo no tiene Bluetooth")
            return
        }
        if (!adaptador.isEnabled) {
            avisar("Enciende el Bluetooth")
            return
        }

        val referencia = FirebaseDatabase.getInstance().getReference("usuarios/$uid/comedero")

        Thread {
            var escuchador: ValueEventListener? = null
            try {
                val hc05 = adaptador.bondedDevices.firstOrNull { it.name == "HC-05" }
                if (hc05 == null) {
                    avisar("Primero empareja el HC-05 en los ajustes del teléfono")
                } else {
                    avisar("Conectando...")
                    val nuevo = hc05.createRfcommSocketToServiceRecord(uuid)
                    nuevo.connect()
                    socket = nuevo

                    referencia.child("estado").onDisconnect().setValue("Desconectado")
                    referencia.child("estado").setValue("Conectado")
                    avisar("Conectado al comedero")

                    escuchador = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var contador = 0
                            for (hijo in snapshot.child("entregas").children) {
                                contador = contador + 1
                            }
                            totalEntregas = contador

                            val orden = snapshot.child("orden").getValue(Boolean::class.java)
                            if (orden == true) {
                                referencia.child("orden").setValue(false)
                                try {
                                    nuevo.outputStream.write("D".toByteArray())
                                } catch (e: Exception) {
                                    avisar("No se pudo enviar la orden")
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    }
                    referencia.addValueEventListener(escuchador)

                    val lector = BufferedReader(InputStreamReader(nuevo.inputStream))
                    while (true) {
                        val linea = lector.readLine()
                        if (linea == null) {
                            break
                        }
                        if (linea.startsWith("NIVEL:")) {
                            val nivel = linea.substring(6).trim().toIntOrNull()
                            if (nivel != null) {
                                referencia.child("nivel").setValue(nivel)
                            }
                        }
                        if (linea.startsWith("ENTREGA:")) {
                            var origen = "Botón"
                            if (linea.contains("APP")) {
                                origen = "App"
                            }
                            referencia.child("entregas").push()
                                .setValue(Entrega(totalEntregas + 1, origen))
                        }
                    }
                }
            } catch (e: Exception) {
                avisar("Se perdió la conexión con el comedero")
            }

            if (escuchador != null) {
                referencia.removeEventListener(escuchador)
            }
            socket = null
            referencia.child("estado").setValue("Desconectado")
        }.start()
    }
}
