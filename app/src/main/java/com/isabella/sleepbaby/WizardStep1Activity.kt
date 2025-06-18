package com.isabella.sleepbaby

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*
import android.content.res.Resources
import android.widget.EditText
import android.widget.NumberPicker

class WizardStep1Activity : AppCompatActivity() {

    private lateinit var datePicker: DatePicker
    private lateinit var buttonNext: Button
    private lateinit var buttonVoltar: Button

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wizard_step1)

        datePicker = findViewById(R.id.datePicker)
        buttonNext = findViewById(R.id.buttonNext)
        buttonVoltar = findViewById(R.id.buttonVoltar)

        // 🔥 Deixar os números do calendário mais escuros (preto)
        setDatePickerTextColor(datePicker)

        buttonNext.setOnClickListener {
            salvarDataNascimento()
        }

        buttonVoltar.setOnClickListener {
            logoutEVoltarProLogin()
        }
    }

    private fun salvarDataNascimento() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            return
        }

        val dia = datePicker.dayOfMonth
        val mes = datePicker.month + 1
        val ano = datePicker.year

        val dataFormatada = String.format("%04d-%02d-%02d", ano, mes, dia)

        val userDoc = firestore.collection("users").document(user.uid)
        val data = hashMapOf("dataNascimento" to dataFormatada)

        userDoc.set(data, SetOptions.merge())
            .addOnSuccessListener {
                startActivity(Intent(this, SexoBebeActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun logoutEVoltarProLogin() {
        val googleSignInClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )

        auth.signOut()
        googleSignInClient.revokeAccess().addOnCompleteListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }


    private fun setDatePickerTextColor(datePicker: DatePicker) {
        try {
            val fields = arrayOf(
                Resources.getSystem().getIdentifier("day", "id", "android"),
                Resources.getSystem().getIdentifier("month", "id", "android"),
                Resources.getSystem().getIdentifier("year", "id", "android")
            )

            for (fieldId in fields) {
                val numberPicker = datePicker.findViewById<NumberPicker>(fieldId)
                numberPicker?.let {
                    val count = it.childCount
                    for (i in 0 until count) {
                        val child = it.getChildAt(i)
                        if (child is EditText) {
                            child.setTextColor(resources.getColor(android.R.color.black))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
