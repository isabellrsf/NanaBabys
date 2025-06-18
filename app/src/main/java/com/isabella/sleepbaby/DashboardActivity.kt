package com.isabella.sleepbaby

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.InputStream
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var imageViewPerfil: ImageView
    private lateinit var buttonAlterarFoto: Button
    private lateinit var buttonSono: Button
    private lateinit var buttonAlimentacao: Button
    private lateinit var buttonRelatorios: Button
    private lateinit var buttonLogout: Button

    private lateinit var textViewNomeBebe: TextView
    private lateinit var textViewIdadeBebe: TextView

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private var imageUri: Uri? = null

    // Launcher para abrir a galeria
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            uploadImageToFirestore()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Inicializar elementos do layout
        imageViewPerfil = findViewById(R.id.imageViewPerfil)
        buttonAlterarFoto = findViewById(R.id.buttonAlterarFoto)
        buttonSono = findViewById(R.id.buttonSono)
        buttonAlimentacao = findViewById(R.id.buttonAlimentacao)
        buttonRelatorios = findViewById(R.id.buttonRelatorios)
        buttonLogout = findViewById(R.id.buttonLogout)

        textViewNomeBebe = findViewById(R.id.textViewNomeBebe)
        textViewIdadeBebe = findViewById(R.id.textViewIdadeBebe)

        // Carregar dados do usuário
        carregarDadosUsuario()

        // Clique para alterar foto
        buttonAlterarFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        imageViewPerfil.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Clique para Registrar Sono
        buttonSono.setOnClickListener {
            startActivity(Intent(this, RegistroSonoActivity::class.java))
        }

        // Clique para Registrar Alimentação
        buttonAlimentacao.setOnClickListener {
            startActivity(Intent(this, AlimentacaoActivity::class.java))
        }

        // Clique para acessar Relatórios
        buttonRelatorios.setOnClickListener {
            startActivity(Intent(this, RelatoriosActivity::class.java))
        }

        // Logout correto
        buttonLogout.setOnClickListener {
            logoutUsuario()
        }
    }

    // 🔥 Logout completo, Firebase + Google
    private fun logoutUsuario() {
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

    // 🔥 Carregar dados do usuário (nome, idade e imagem)
    private fun carregarDadosUsuario() {
        val user = auth.currentUser ?: return

        val userDoc = firestore.collection("users").document(user.uid)

        userDoc.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val nome = document.getString("nome")
                val dataNascimento = document.getString("dataNascimento")
                val base64Image = document.getString("profileImage")

                textViewNomeBebe.text = nome ?: "Nome não cadastrado"

                if (!dataNascimento.isNullOrEmpty()) {
                    val idade = calcularIdadeEmMeses(dataNascimento)
                    textViewIdadeBebe.text = "Idade: $idade meses"
                } else {
                    textViewIdadeBebe.text = "Idade não cadastrada"
                }

                if (!base64Image.isNullOrEmpty()) {
                    val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    imageViewPerfil.setImageBitmap(bitmap)
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔥 Calcular idade a partir da data
    private fun calcularIdadeEmMeses(dataNascimento: String): Int {
        return try {
            val partes = dataNascimento.split("-")
            val ano = partes[0].toInt()
            val mes = partes[1].toInt()

            val hoje = Calendar.getInstance()
            val anoAtual = hoje.get(Calendar.YEAR)
            val mesAtual = hoje.get(Calendar.MONTH) + 1

            val anos = anoAtual - ano
            val meses = (anos * 12) + (mesAtual - mes)

            if (meses < 0) 0 else meses
        } catch (e: Exception) {
            0
        }
    }

    // 🔥 Converter URI da imagem para Base64
    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                Base64.encodeToString(bytes, Base64.DEFAULT)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 🔥 Upload da imagem para Firestore
    private fun uploadImageToFirestore() {
        val user = auth.currentUser ?: return

        if (imageUri == null) {
            Toast.makeText(this, "Nenhuma imagem selecionada!", Toast.LENGTH_SHORT).show()
            return
        }

        val base64Image = uriToBase64(imageUri!!)
        if (base64Image == null) {
            Toast.makeText(this, "Erro ao converter imagem", Toast.LENGTH_SHORT).show()
            return
        }

        val userDoc = firestore.collection("users").document(user.uid)

        val data = hashMapOf("profileImage" to base64Image)

        userDoc.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Foto atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                carregarDadosUsuario()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar imagem: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
    }
}
