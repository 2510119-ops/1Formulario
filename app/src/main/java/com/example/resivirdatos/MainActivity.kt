package com.example.resivirdatos

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import java.text.Normalizer
import java.util.Calendar
import java.util.Locale

//Juan Jose Ortiz Ortiz 29/01/2026
//Kevin Daniel Lozada Saldibar
//Enrique Morgado Montiel
class MainActivity : AppCompatActivity() {

    companion object {
        // This regex matches all combined diacritical marks.
        private val DIACRITICS_REGEX = Regex("\\p{InCombiningDiacriticalMarks}+")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etNombre = findViewById<TextInputEditText>(R.id.etNombre)
        val etMatricula = findViewById<TextInputEditText>(R.id.etMatricula)
        val etAsignatura = findViewById<TextInputEditText>(R.id.etAsignatura)
        val etHora = findViewById<TextInputEditText>(R.id.etHora)
        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                etFecha.setText(date)
            }, year, month, day).show()
        }

        etHora.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                etHora.setText(time)
            }, hour, minute, true).show()
        }

        btnEnviar.setOnClickListener {
            etMatricula.error = null

            val nombreRaw = etNombre.text.toString()
            val matricula = etMatricula.text.toString()
            val asignaturaRaw = etAsignatura.text.toString()
            val hora = etHora.text.toString()
            val fecha = etFecha.text.toString()

            val nombre = quitarAcentos(nombreRaw)
            val asignatura = quitarAcentos(asignaturaRaw)

            when {
                nombre.isEmpty() || matricula.isEmpty() || asignatura.isEmpty() || hora.isEmpty() || fecha.isEmpty() -> {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }

                matricula.toLongOrNull() == null -> {
                    etMatricula.error = "La matrícula solo debe contener números"
                    Toast.makeText(this, "El formato de la matrícula es incorrecto", Toast.LENGTH_SHORT).show()
                }

                matricula.length > 10 -> {
                    etMatricula.error = "No debe exceder los 10 dígitos"
                    Toast.makeText(this, "La matrícula es demasiado larga", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val mensaje = """
                        Datos Ingresados:
                        Nombre: $nombre
                        Matrícula: $matricula
                        Asignatura: $asignatura
                        Hora: $hora
                        Fecha: $fecha
                    """.trimIndent()

                    AlertDialog.Builder(this)
                        .setTitle("Resumen de Registro")
                        .setMessage(mensaje)
                        .setPositiveButton("Aceptar") { dialog, _ ->
                            etNombre.setText("")
                            etMatricula.setText("")
                            etAsignatura.setText("")
                            etHora.setText("")
                            etFecha.setText("")
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
    }

    private fun quitarAcentos(texto: String): String {
        val normalized = Normalizer.normalize(texto, Normalizer.Form.NFD)
        return DIACRITICS_REGEX.replace(normalized, "")
    }
}
