package com.gap.presentation.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gap.presentation.R
import com.gap.presentation.databinding.FragmentHomeBinding
import com.gap.presentation.databinding.FragmentReportBinding
import com.gap.presentation.main.viewModels.HomeViewModel


class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("ReportFragment == null")
    private val navController by lazy { findNavController() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workWithButtons()
    }

    private fun workWithButtons() {
        with(binding) {
            ibBack.setOnClickListener {
                navController.navigate(R.id.action_reportFragment_to_homeFragment)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}