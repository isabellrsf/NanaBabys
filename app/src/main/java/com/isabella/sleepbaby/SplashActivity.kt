package com.isabella.sleepbaby

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        val logo = findViewById<ImageView>(R.id.imageLogo)

        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1000
            fillAfter = true
        }

        logo.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            verificarFluxo()
        }, 2000)
    }

    private fun verificarFluxo() {
        val user = auth.currentUser

        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            val userDoc = firestore.collection("users").document(user.uid)
            userDoc.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val dataNascimento = document.getString("dataNascimento")
                    val sexo = document.getString("sexo")
                    val nome = document.getString("nome")

                    when {
                        dataNascimento.isNullOrEmpty() -> {
                            startActivity(Intent(this, WizardStep1Activity::class.java))
                        }
                        sexo.isNullOrEmpty() -> {
                            startActivity(Intent(this, SexoBebeActivity::class.java))
                        }
                        nome.isNullOrEmpty() -> {
                            startActivity(Intent(this, NomeBebeActivity::class.java))
                        }
                        else -> {
                            startActivity(Intent(this, DashboardActivity::class.java))
                        }
                    }
                } else {
                    startActivity(Intent(this, WizardStep1Activity::class.java))
                }
                finish()
            }.addOnFailureListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}
