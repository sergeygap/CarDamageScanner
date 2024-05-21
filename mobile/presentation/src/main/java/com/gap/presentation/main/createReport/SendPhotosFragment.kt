package com.gap.presentation.main.createReport

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.gap.presentation.R
import com.gap.presentation.databinding.CustomDialogBinding
import com.gap.presentation.databinding.FragmentSendPhotosBinding
import com.gap.presentation.main.createReport.viewModel.SendPhotosViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
    private val galleryPermissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onGotPermissionsResultForGallery
    )

    private lateinit var resultLauncherCameraFrontPart: ActivityResultLauncher<Intent>
    private lateinit var resultLauncherCameraLeftPart: ActivityResultLauncher<Intent>
    private lateinit var resultLauncherCameraBackPart: ActivityResultLauncher<Intent>
    private lateinit var resultLauncherCameraRightPart: ActivityResultLauncher<Intent>

    private lateinit var resultLauncherGalleryFrontPart: ActivityResultLauncher<Intent>
    private lateinit var resultLauncherGalleryLeftPart: ActivityResultLauncher<Intent>
    private lateinit var resultLauncherGalleryBackPart: ActivityResultLauncher<Intent>
    private lateinit var resultLauncherGalleryRightPart: ActivityResultLauncher<Intent>

    private val listPNGFiles = mutableListOf<File>()
    private lateinit var imageViews: List<ImageView>
    private val viewModel: SendPhotosViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialCode()
        workWithUI()
    }

    private fun initialCode() {
        resultLauncherCameraFrontPart = resultLauncherCamera(binding.ivFrontPart)
        resultLauncherCameraLeftPart = resultLauncherCamera(binding.ivLeftPart)
        resultLauncherCameraBackPart = resultLauncherCamera(binding.ivBackPart)
        resultLauncherCameraRightPart = resultLauncherCamera(binding.ivRightPart)
        resultLauncherGalleryFrontPart = resultLauncherGallery(binding.ivFrontPart)
        resultLauncherGalleryLeftPart = resultLauncherGallery(binding.ivLeftPart)
        resultLauncherGalleryBackPart = resultLauncherGallery(binding.ivBackPart)
        resultLauncherGalleryRightPart = resultLauncherGallery(binding.ivRightPart)
        imageViews = listOf(
            binding.ivBackPart,
            binding.ivFrontPart,
            binding.ivLeftPart,
            binding.ivRightPart
        )
    }

    private fun workWithUI() {
        with(binding) {
            btnSend.setOnClickListener {
                viewModel.exchangeFiles(listPNGFiles)
                Toast.makeText(requireContext(), "SEND", Toast.LENGTH_SHORT).show()
                listPNGFiles.forEach { file ->
                    Log.d(TAG, "File: ${file.name}, Size: ${file.length()} bytes")
                }
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
        viewModel.timeLD.observe(viewLifecycleOwner) {
            Log.d(TAG, "workWithUI: ${it.forEach { reportItem ->
                reportItem.toString()
            }}")
        }
    }

    private fun checkFullFileForButton() {
        binding.btnSend.isEnabled = imageViews.all { it.drawable != null }
    }


    private fun setOnClickListenersInCustomDialog(binding: CustomDialogBinding) {
        listOf(binding.ivSelectPhoto, binding.tvSelectPhoto).forEach {
            it.setOnClickListener {
                galleryPermissionRequestLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                clauseAlertDialog()
            }
        }
        listOf(binding.ivTakePhoto, binding.tvTakePhoto).forEach {
            it.setOnClickListener {
                cameraPermissionRequestLauncher.launch(Manifest.permission.CAMERA)
                clauseAlertDialog()
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

    private fun onGotPermissionsResultForGallery(grantResult: Boolean) {
        if (grantResult) {
            onGalleryPermissionGranted()
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onGalleryPermissionGranted() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        when (flag) {
            PartOfCars.FRONT -> resultLauncherGalleryFrontPart.launch(intent)
            PartOfCars.LEFT -> resultLauncherGalleryLeftPart.launch(intent)
            PartOfCars.BACK -> resultLauncherGalleryBackPart.launch(intent)
            PartOfCars.RIGHT -> resultLauncherGalleryRightPart.launch(intent)
            PartOfCars.SOMETHING -> Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onCameraPermissionGranted() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        when (flag) {
            PartOfCars.FRONT -> resultLauncherCameraFrontPart.launch(intent)
            PartOfCars.LEFT -> resultLauncherCameraLeftPart.launch(intent)
            PartOfCars.BACK -> resultLauncherCameraBackPart.launch(intent)
            PartOfCars.RIGHT -> resultLauncherCameraRightPart.launch(intent)
            PartOfCars.SOMETHING -> Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT)
                .show()
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

    private fun clauseAlertDialog() {
        alertDialog?.dismiss()
        alertDialog = null
    }

    private fun resultLauncherCamera(inputIV: ImageView): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as? Bitmap
                imageBitmap?.let {
                    inputIV.setImageBitmap(it)
                    saveBitmapToFile(it, fileNames(flag))
                    checkFullFileForButton()
                }
            }
            flag = PartOfCars.SOMETHING
        }
    }


    private fun resultLauncherGallery(inputIV: ImageView): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let { uri ->
                    inputIV.setImageURI(uri)
                    val bitmap = getBitmapFromUri(uri)
                    bitmap?.let {
                        try {
                            val filename = fileNames(flag)
                            saveBitmapToFile(it, filename)
                            checkFullFileForButton()
                        } catch (e: IllegalArgumentException) {
                            Log.e(TAG, "Invalid part of car selected", e)
                        }
                    }
                }
            }
            flag = PartOfCars.SOMETHING
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bitmap from Uri", e)
            null
        }
    }


    private fun fileNames(flag: PartOfCars): String {
        return when (flag) {
            PartOfCars.FRONT -> "front.png"
            PartOfCars.LEFT -> "left.png"
            PartOfCars.BACK -> "back.png"
            PartOfCars.RIGHT -> "right.png"
            PartOfCars.SOMETHING -> throw IllegalArgumentException("flag = SOMETHING")
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap, filename: String) {
        val context = requireContext()
        val file = File(context.cacheDir, filename)
        file.createNewFile()

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            listPNGFiles.add(file)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fos?.close()
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