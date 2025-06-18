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

class SonoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val lista = mutableListOf<Sono>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sono, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewSono)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        carregarDados()

        return view
    }

    private fun carregarDados() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()

        val ref = database.getReference("registrosSono").child(userId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(Sono::class.java)
                    item?.let { lista.add(it) }
                }
                recyclerView.adapter = SonoAdapter(lista)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
