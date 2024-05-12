package com.gap.presentation.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.gap.presentation.R
import com.gap.presentation.databinding.FragmentSendPhotosBinding
import com.gap.presentation.databinding.FragmentStartReportBinding


class SendPhotosFragment : Fragment() {

    private var _binding: FragmentSendPhotosBinding? = null
    private val binding: FragmentSendPhotosBinding
        get() = _binding ?: throw RuntimeException("SendPhotosFragment == null")
    private val navController by lazy { findNavController() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workWithUI()
    }

    private fun workWithUI() {
        with(binding) {
            btnSend.setOnClickListener {
                Toast.makeText(requireContext(), "SEND", Toast.LENGTH_SHORT).show()
            }
            binding.ibBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}