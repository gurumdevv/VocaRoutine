package com.gurumlab.vocaroutine.ui.making

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gurumlab.vocaroutine.databinding.ActivityAfterPhotoBinding
import com.gurumlab.vocaroutine.ui.common.Constants

class AfterPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAfterPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAfterPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ocrResult = intent.getStringExtra(Constants.KEY_OCR_RESULT)
        binding.etOcrResult.setText(ocrResult)

        binding.topAppBar.setOnClickListener {
            finish()
        }
    }
}