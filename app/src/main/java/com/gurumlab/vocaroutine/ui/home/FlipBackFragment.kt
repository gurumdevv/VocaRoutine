package com.gurumlab.vocaroutine.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gurumlab.vocaroutine.data.model.Vocabulary
import com.gurumlab.vocaroutine.databinding.FragmentFlipBackBinding
import com.gurumlab.vocaroutine.ui.BaseFragment

class FlipBackFragment: BaseFragment<FragmentFlipBackBinding>() {

    private val args: FlipBackFragmentArgs by navArgs()
    private lateinit var vocabulary: Vocabulary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vocabulary = args.vocabulary
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFlipBackBinding {
        return FragmentFlipBackBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vocabulary = vocabulary

        binding.btnAgain.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}