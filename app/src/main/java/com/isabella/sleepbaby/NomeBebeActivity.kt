package com.isabella.sleepbaby

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class NomeBebeActivity : AppCompatActivity() {

    private lateinit var editTextNome: EditText
    private lateinit var buttonSalvar: Button
    private lateinit var buttonVoltar: Button

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nome_bebe)

        editTextNome = findViewById(R.id.editTextNomeBebe)
        buttonSalvar = findViewById(R.id.buttonSalvarNome)
        buttonVoltar = findViewById(R.id.buttonVoltar)

        // Botão salvar (próximo)
        buttonSalvar.setOnClickListener {
            val nomeBebe = editTextNome.text.toString().trim()

            if (nomeBebe.isEmpty()) {
                Toast.makeText(this, "Por favor, insira o nome do bebê", Toast.LENGTH_SHORT).show()
            } else {
                salvarNomeNoFirestore(nomeBebe)
            }
        }

        // Botão voltar → volta para a tela de Sexo
        buttonVoltar.setOnClickListener {
            val intent = Intent(this, SexoBebeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun salvarNomeNoFirestore(nome: String) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            return
        }

        val userDoc = firestore.collection("users").document(user.uid)
        val data = hashMapOf("nome" to nome)

        userDoc.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Nome salvo com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar nome: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
