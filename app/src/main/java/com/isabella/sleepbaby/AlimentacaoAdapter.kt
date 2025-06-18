package com.isabella.sleepbaby

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlimentacaoAdapter(private val lista: List<Alimentacao>) :
    RecyclerView.Adapter<AlimentacaoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tipo: TextView = view.findViewById(R.id.textViewTipo)
        val quantidade: TextView = view.findViewById(R.id.textViewQuantidade)
        val horario: TextView = view.findViewById(R.id.textViewHorario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alimentacao, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.tipo.text = "Tipo: ${item.tipo}"
        holder.quantidade.text = "Quantidade: ${item.quantidade}"
        holder.horario.text = "Horário: ${item.horario}"
    }
}
