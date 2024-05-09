package com.gap.presentation.welcome

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
import com.gap.presentation.R
import com.gap.presentation.databinding.CustomDialogBinding
import com.gap.presentation.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding: FragmentWelcomeBinding
        get() = _binding ?: throw RuntimeException("WelcomeFragment == null")

    private val cameraPermissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onGotPermissionsResultForCamera
    )
    private val resultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.ivEnterPhoto.setImageBitmap(imageBitmap)
            }
        }

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
            }
        }
    }

    private fun createAlertDialog(): CustomDialogBinding {
        val dialogBinding = CustomDialogBinding.inflate(layoutInflater)
        AlertDialog.Builder(
            requireContext(),
            R.style.RoundedCornersDialog
        ).setView(dialogBinding.root).create().show()
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
        resultLauncherCamera.launch(intent)
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

    companion object {
        private const val TAG = "WelcomeFragmentLog"
    }
}