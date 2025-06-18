package com.isabella.sleepbaby

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class RelatorioPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AlimentacaoFragment()
            1 -> SonoFragment()
            else -> throw IllegalArgumentException("Posição inválida")
        }
    }
}
