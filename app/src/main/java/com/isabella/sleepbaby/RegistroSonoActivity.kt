package com.isabella.sleepbaby

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RegistroSonoActivity : AppCompatActivity() {

    private lateinit var editTextInicio: EditText
    private lateinit var editTextFim: EditText
    private lateinit var buttonRegistrar: AppCompatButton
    private lateinit var buttonVoltar: AppCompatButton

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_sono)

        editTextInicio = findViewById(R.id.editTextInicio)
        editTextFim = findViewById(R.id.editTextFim)
        buttonRegistrar = findViewById(R.id.buttonRegistrarSono)
        buttonVoltar = findViewById(R.id.buttonVoltar)

        // Clique nos campos para abrir TimePicker
        editTextInicio.setOnClickListener {
            abrirTimePicker(editTextInicio)
        }

        editTextFim.setOnClickListener {
            abrirTimePicker(editTextFim)
        }

        // Ação do botão Registrar
        buttonRegistrar.setOnClickListener {
            val inicio = editTextInicio.text.toString().trim()
            val fim = editTextFim.text.toString().trim()

            if (inicio.isEmpty() || fim.isEmpty()) {
                Toast.makeText(this, "Preencha os dois horários!", Toast.LENGTH_SHORT).show()
            } else {
                salvarSonoFirebase(inicio, fim)
            }
        }

        // Ação do botão Voltar
        buttonVoltar.setOnClickListener {
            finish()
        }
    }

    private fun abrirTimePicker(editText: EditText) {
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minuto = calendario.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val horaFormatada = String.format("%02d:%02d", selectedHour, selectedMinute)
                editText.setText(horaFormatada)
            },
            hora, minuto, true
        )
        timePicker.show()
    }

    private fun salvarSonoFirebase(inicio: String, fim: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val referencia = database.getReference("registrosSono").child(userId)

            val dadosSono = mapOf(
                "inicio" to inicio,
                "fim" to fim
            )

            referencia.push().setValue(dadosSono)
                .addOnSuccessListener {
                    Toast.makeText(this, "Sono registrado com sucesso!", Toast.LENGTH_SHORT).show()
                    editTextInicio.text.clear()
                    editTextFim.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao salvar no Firebase.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}
