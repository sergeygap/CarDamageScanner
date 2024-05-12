package com.gap.presentation.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gap.presentation.R
import com.gap.presentation.autorization.InputCodeFragment
import com.gap.presentation.databinding.FragmentHomeBinding
import com.gap.presentation.databinding.FragmentInputCodeBinding
import com.gap.presentation.main.adapters.HomeAdapter

class HomeFragment : Fragment() {

    private lateinit var adapter: HomeAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("HomeFragment == null")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveStartFragment()
        workWithRV()
    }

    private fun workWithRV() {
        adapter = HomeAdapter()
        binding.recyclerView.adapter = adapter
    }

    private fun saveStartFragment() {
        val sharedPreferences = requireActivity()
            .getSharedPreferences(START_FRAGMENT_TAG, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(REACHED_HOME_FRAGMENT, true)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val START_FRAGMENT_TAG = "start_fragment_tag"
        const val REACHED_HOME_FRAGMENT = "reachedHomeFragment"
    }
}