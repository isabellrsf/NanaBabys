package com.isabella.sleepbaby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AlimentacaoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val lista = mutableListOf<Alimentacao>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alimentacao, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAlimentacao)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        carregarDados()

        return view
    }

    private fun carregarDados() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()

        val ref = database.getReference("alimentacoes").child(userId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(Alimentacao::class.java)
                    item?.let { lista.add(it) }
                }
                recyclerView.adapter = AlimentacaoAdapter(lista)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
