package com.gap.presentation.welcome

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.gap.presentation.R
import com.gap.presentation.databinding.CustomDialogBinding
import com.gap.presentation.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding: FragmentWelcomeBinding
        get() = _binding ?: throw RuntimeException("WelcomeFragment == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workWithUI()
    }

    private fun workWithUI() {
        with(binding) {
            btnWelcome.setOnClickListener {
                openGalleryOrTakePhoto()
            }
        }
    }

    private fun openGalleryOrTakePhoto() {
        val dialogBinding = CustomDialogBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(
            requireContext(),
            R.style.RoundedCornersDialog
        ).setView(dialogBinding.root).create()
        alertDialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}