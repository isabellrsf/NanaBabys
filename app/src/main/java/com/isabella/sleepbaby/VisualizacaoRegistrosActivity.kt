package com.isabella.sleepbaby

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.isabella.sleepbaby.Alimentacao
import com.isabella.sleepbaby.Sono
import com.isabella.sleepbaby.AlimentacaoAdapter
import com.isabella.sleepbaby.SonoAdapter

class VisualizacaoRegistrosActivity : AppCompatActivity() {

    private lateinit var recyclerAlimentacao: RecyclerView
    private lateinit var recyclerSono: RecyclerView

    private val alimentacoes = mutableListOf<Alimentacao>()
    private val registrosSono = mutableListOf<Sono>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizacao_registros)

        recyclerAlimentacao = findViewById(R.id.recyclerViewAlimentacao)
        recyclerSono = findViewById(R.id.recyclerViewSono)

        recyclerAlimentacao.layoutManager = LinearLayoutManager(this)
        recyclerSono.layoutManager = LinearLayoutManager(this)

        carregarDados()
    }

    private fun carregarDados() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseDatabase.getInstance()

        // Alimentações
        val refAlimentacao = db.getReference("alimentacoes").child(userId)
        refAlimentacao.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                alimentacoes.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(Alimentacao::class.java)
                    item?.let { alimentacoes.add(it) }
                }
                recyclerAlimentacao.adapter = AlimentacaoAdapter(alimentacoes)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Sono
        val refSono = db.getReference("registrosSono").child(userId)
        refSono.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                registrosSono.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(Sono::class.java)
                    item?.let { registrosSono.add(it) }
                }
                recyclerSono.adapter = SonoAdapter(registrosSono)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
