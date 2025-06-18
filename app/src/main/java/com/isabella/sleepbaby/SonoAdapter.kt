package com.isabella.sleepbaby

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SonoAdapter(private val lista: List<Sono>) :
    RecyclerView.Adapter<SonoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewInicio: TextView = view.findViewById(R.id.textViewInicio)
        val textViewFim: TextView = view.findViewById(R.id.textViewFim)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sono, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.textViewInicio.text = "Início: ${item.inicio}"
        holder.textViewFim.text = "Fim: ${item.fim}"
    }
}
