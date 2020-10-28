package com.example.textrecognition.Fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.textrecognition.Controllers.StaticRecognitionViewModel
import com.example.textrecognition.R
import com.google.android.material.snackbar.Snackbar
import java.io.IOException


class RecognitionSelectionFragment : Fragment () {
    val REQUEST_IMAGE_OPEN = 1
    private val model: StaticRecognitionViewModel by this.activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recognition_selection, container, false)
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)

        view.findViewById<Button>(R.id.static_detection).setOnClickListener{
            requestPermission(view.findViewById(R.id.recognition_selection_layout))
        }

        view.findViewById<Button>(R.id.live_detection).setOnClickListener{
            findNavController().navigate(R.id.LiveRecognitionFragment)
        }
    }


    private fun hasReadExternalStoragePermission() =
        ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(view: View) {
        when {
            hasReadExternalStoragePermission() -> {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_OPEN);
                } else {
                    Log.d("ImplicitIntents", "Can't handle this intent!");
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                // TODO: fix this implementation to show properly
                Snackbar.make(
                    view,
                    "Needs Storage Permission to work",
                    Snackbar.LENGTH_LONG).setAction("ENABLE") {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        0
                    )
                }
            }
            else -> {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri? = data?.data
            // Do work with full size photo saved at fullPhotoUri
            val selectedImage: Bitmap? = loadFromUri(fullPhotoUri!!)
            model.imageBitmap = selectedImage!!
            model.imageURI = fullPhotoUri!!
            findNavController().navigate(R.id.StaticRecognitionFragment)
        }
        Log.i("SELECTING_IMAGE_RESULT", data?.dataString)
    }

    private fun loadFromUri(photoUri: Uri): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(requireActivity().contentResolver, photoUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }
}