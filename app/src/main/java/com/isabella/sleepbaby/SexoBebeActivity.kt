package com.isabella.sleepbaby

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SexoBebeActivity : AppCompatActivity() {

    private lateinit var buttonMenino: Button
    private lateinit var buttonMenina: Button
    private lateinit var buttonContinuar: Button
    private lateinit var buttonVoltar: Button

    private var sexoSelecionado: String? = null

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sexo_bebe)

        // Inicializando botões
        buttonMenino = findViewById(R.id.buttonMenino)
        buttonMenina = findViewById(R.id.buttonMenina)
        buttonContinuar = findViewById(R.id.buttonContinuar)
        buttonVoltar = findViewById(R.id.buttonVoltar)

        // Seleção de sexo
        buttonMenino.setOnClickListener {
            sexoSelecionado = "Masculino"
            marcarSelecionado("Masculino")
        }

        buttonMenina.setOnClickListener {
            sexoSelecionado = "Feminino"
            marcarSelecionado("Feminino")
        }

        // Continuar
        buttonContinuar.setOnClickListener {
            if (sexoSelecionado == null) {
                Toast.makeText(this, "Selecione uma opção!", Toast.LENGTH_SHORT).show()
            } else {
                salvarSexoNoFirestore()
            }
        }

        // Voltar para a tela de Data
        buttonVoltar.setOnClickListener {
            val intent = Intent(this, WizardStep1Activity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun salvarSexoNoFirestore() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            return
        }

        val userDoc = firestore.collection("users").document(user.uid)
        val data = hashMapOf("sexo" to sexoSelecionado)

        userDoc.set(data, SetOptions.merge())
            .addOnSuccessListener {
                startActivity(Intent(this, NomeBebeActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar sexo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun marcarSelecionado(sexo: String) {
        buttonMenino.setBackgroundResource(R.drawable.button_background_login)
        buttonMenina.setBackgroundResource(R.drawable.button_background_login)

        when (sexo) {
            "Masculino" -> buttonMenino.setBackgroundResource(R.drawable.button_back_background)
            "Feminino" -> buttonMenina.setBackgroundResource(R.drawable.button_back_background)
        }
    }
}
