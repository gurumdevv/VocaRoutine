package com.gurumlab.vocaroutine.ui.making

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.databinding.ActivityScanBinding
import com.websitebeaver.documentscanner.DocumentScanner
import com.websitebeaver.documentscanner.constants.ResponseType

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private lateinit var documentScanner: DocumentScanner
    private lateinit var recognizer: TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

        documentScanner = DocumentScanner(
            this,
            { croppedImageResults ->
                val bitmap = BitmapFactory.decodeFile(croppedImageResults.first())
                binding.ivCroppedImage.setImageBitmap(bitmap)

                val image = InputImage.fromBitmap(bitmap, 0)

                val result = recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        TODO("visionText.text로 fragment에 값 전달하기")
                    }
                    .addOnFailureListener { e ->
                        Snackbar.make(
                            binding.root.rootView,
                            getString(R.string.please_scan_again), Snackbar.LENGTH_SHORT
                        ).show()
                        finish()
                    }

            },
            {
                Snackbar.make(
                    binding.root.rootView,
                    getString(R.string.please_scan_again), Snackbar.LENGTH_SHORT
                ).show()
                finish()
            },
            {
                finish()
            },
            ResponseType.IMAGE_FILE_PATH,
            true,
            1
        )

        documentScanner.startScan()
    }
}