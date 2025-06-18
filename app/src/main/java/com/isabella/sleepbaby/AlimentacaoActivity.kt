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

class AlimentacaoActivity : AppCompatActivity() {

    private lateinit var editTextTipo: EditText
    private lateinit var editTextQuantidade: EditText
    private lateinit var editTextHorario: EditText
    private lateinit var buttonRegistrar: AppCompatButton
    private lateinit var buttonVoltar: AppCompatButton

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alimentacao)

        // Referências
        editTextTipo = findViewById(R.id.editTextTipoAlimentacao)
        editTextQuantidade = findViewById(R.id.editTextQuantidade)
        editTextHorario = findViewById(R.id.editTextHorario)
        buttonRegistrar = findViewById(R.id.buttonRegistrarAlimentacao)
        buttonVoltar = findViewById(R.id.buttonVoltar)

        // TimePicker no campo de horário
        editTextHorario.setOnClickListener {
            abrirTimePicker()
        }

        // Ação Registrar
        buttonRegistrar.setOnClickListener {
            val tipo = editTextTipo.text.toString().trim()
            val quantidade = editTextQuantidade.text.toString().trim()
            val horario = editTextHorario.text.toString().trim()

            if (tipo.isEmpty() || quantidade.isEmpty() || horario.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                salvarNoFirebase(tipo, quantidade, horario)
            }
        }

        // Ação Voltar
        buttonVoltar.setOnClickListener {
            finish()
        }
    }

    private fun abrirTimePicker() {
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minuto = calendario.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                val horaFormatada = String.format("%02d:%02d", selectedHour, selectedMinute)
                editTextHorario.setText(horaFormatada)
            }, hora, minuto, true
        )

        timePicker.show()
    }

    private fun salvarNoFirebase(tipo: String, quantidade: String, horario: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val referencia = database.getReference("alimentacoes").child(userId)

            val dados = mapOf(
                "tipo" to tipo,
                "quantidade" to quantidade,
                "horario" to horario
            )

            referencia.push().setValue(dados)
                .addOnSuccessListener {
                    Toast.makeText(this, "Alimentação registrada com sucesso!", Toast.LENGTH_SHORT).show()
                    editTextTipo.text.clear()
                    editTextQuantidade.text.clear()
                    editTextHorario.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao registrar no Firebase.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}
