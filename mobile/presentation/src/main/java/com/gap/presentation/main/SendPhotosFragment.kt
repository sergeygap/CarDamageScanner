package com.gap.presentation.main

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gap.presentation.R
import com.gap.presentation.databinding.CustomDialogBinding
import com.gap.presentation.databinding.FragmentSendPhotosBinding


class SendPhotosFragment : Fragment() {

    private var _binding: FragmentSendPhotosBinding? = null
    private val binding: FragmentSendPhotosBinding
        get() = _binding ?: throw RuntimeException("SendPhotosFragment == null")
    private val navController by lazy { findNavController() }
    private var alertDialog: AlertDialog? = null
    private var flag: PartOfCars = PartOfCars.SOMETHING

    private val cameraPermissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onGotPermissionsResultForCamera
    )

    private val resultLauncherCameraFrontPart =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.ivFrontPart.setImageBitmap(imageBitmap)
            }
            flag = PartOfCars.SOMETHING
        }
    private val resultLauncherCameraLeftPart =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.ivLeftPart.setImageBitmap(imageBitmap)
            }
            flag = PartOfCars.SOMETHING
        }
    private val resultLauncherCameraBackPart =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.ivBackPart.setImageBitmap(imageBitmap)
            }
            flag = PartOfCars.SOMETHING
        }
    private val resultLauncherCameraRightPart =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.ivRightPart.setImageBitmap(imageBitmap)
            }
            flag = PartOfCars.SOMETHING
        }

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

            binding.ivFrontPart.setOnClickListener {
                flag = PartOfCars.FRONT
                setOnClickListenersInCustomDialog(createAlertDialog())
            }
            binding.ivLeftPart.setOnClickListener {
                flag = PartOfCars.LEFT
                setOnClickListenersInCustomDialog(createAlertDialog())
            }
            binding.ivBackPart.setOnClickListener {
                flag = PartOfCars.BACK
                setOnClickListenersInCustomDialog(createAlertDialog())
            }
            binding.ivRightPart.setOnClickListener {
                flag = PartOfCars.RIGHT
                setOnClickListenersInCustomDialog(createAlertDialog())
            }

        }
    }


    private fun setOnClickListenersInCustomDialog(binding: CustomDialogBinding) {
        listOf(binding.ivSelectPhoto, binding.tvSelectPhoto).forEach {
            it.setOnClickListener {
//                checkPermissionsForGallery()
            }
        }
        listOf(binding.ivTakePhoto, binding.tvTakePhoto).forEach {
            it.setOnClickListener {
                cameraPermissionRequestLauncher.launch(Manifest.permission.CAMERA)
                alertDialog?.dismiss()
                alertDialog = null
            }
        }
    }

    private fun createAlertDialog(): CustomDialogBinding {
        val dialogBinding = CustomDialogBinding.inflate(layoutInflater)
        alertDialog = AlertDialog.Builder(
            requireContext(),
            R.style.RoundedCornersDialog
        ).setView(dialogBinding.root).create().apply {
            show()
        }
        return dialogBinding
    }


    private fun onGotPermissionsResultForCamera(grantResults: Boolean) {
        Log.d(TAG, "onRequestPermissionsResult: working")
        if (grantResults) {
            onCameraPermissionGranted()
        } else {
            /**
            Запретили навсегда или нет, true -> показать объяснения, false -> навсегда
             */
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_SHORT)
                    .show()
            } else {
                askUserForOpeningAppSettings()
            }
        }
    }

    private fun onCameraPermissionGranted() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        when(flag) {
            PartOfCars.FRONT -> resultLauncherCameraFrontPart.launch(intent)
            PartOfCars.LEFT -> resultLauncherCameraLeftPart.launch(intent)
            PartOfCars.BACK -> resultLauncherCameraBackPart.launch(intent)
            PartOfCars.RIGHT -> resultLauncherCameraRightPart.launch(intent)
            PartOfCars.SOMETHING -> Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askUserForOpeningAppSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        )
        if (requireActivity().packageManager.resolveActivity(
                appSettingsIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            Toast.makeText(
                requireActivity(),
                "Разрешение на камеру отклонено навсегда",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.permission_denied)
                .setMessage(R.string.permission_denied_forever_message)
                .setPositiveButton(R.string.open) { _, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class PartOfCars {
        FRONT, LEFT, BACK, RIGHT, SOMETHING
    }

    companion object {
        private const val TAG = "SendPhotosFragmentLog"
    }
}