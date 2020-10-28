package com.example.textrecognition.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.textrecognition.Controllers.StaticRecognitionViewModel
import com.example.textrecognition.R
import com.google.android.material.snackbar.Snackbar
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.text.MLLocalTextSetting
import com.huawei.hms.mlsdk.text.MLText
import java.io.IOException


class StaticRecognitionFragment : Fragment() {
    private var analyzer = MLAnalyzerFactory.getInstance().localTextAnalyzer

    private val model: StaticRecognitionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_static_recognition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.text_image).setImageURI(model.imageURI)
        view.findViewById<Button>(R.id.text_detect).setOnClickListener {
            localAnalyzer()
        }
    }

    private fun localAnalyzer() {
        // Create the text analyzer MLTextAnalyzer to recognize characters in images. You can set MLLocalTextSetting to
        // specify languages that can be recognized.
        // Use the customized parameter MLLocalTextSetting to configure the text analyzer on the device.
        val setting = MLLocalTextSetting.Factory().setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
            .setLanguage("en")
            .create()
        analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting)

        val frame = MLFrame.fromBitmap(model.imageBitmap)
        val task = analyzer.asyncAnalyseFrame(frame)
        task.addOnSuccessListener { text ->
            displaySuccess(text)
        }.addOnFailureListener {
            displayFailure()
        }
    }

    private fun displayFailure() {
        view?.findViewById<TextView>(R.id.text_result)?.text = "Failure"
    }

    private fun displaySuccess(mlText: MLText) {
        var result = ""
        val blocks = mlText.blocks
        for (block in blocks) {
            for (line in block.contents) {
                result += """${line.stringValue}""".trimIndent()
            }
        }
        view?.findViewById<TextView>(R.id.text_result)?.text = result
    }

    override fun onDestroy() {
        super.onDestroy()
        if (analyzer == null) {
            return
        }
        try {
            analyzer.stop()
        } catch (e: IOException) {
            Log.e(
                "STATIC_RECOGNITION",
                "Stop failed: " + e.message
            )
        }
    }
}