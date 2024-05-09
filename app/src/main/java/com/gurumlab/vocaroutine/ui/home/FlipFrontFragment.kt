package com.gurumlab.vocaroutine.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.Vocabulary
import com.gurumlab.vocaroutine.databinding.FragmentFlipFrontBinding
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.ui.common.EventObserver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue

class FlipFrontFragment : BaseFragment<FragmentFlipFrontBinding>() {

    private lateinit var viewModel: HomeViewModel
    private var title = ""
    private var createdDate = ""
    private var isLoadList = false
    private val vocabularies = mutableListOf<Vocabulary>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFlipFrontBinding {
        return FragmentFlipFrontBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        setReview()
        setButtonClickListener()
    }

    private fun setReview() {
        if (!isLoadList) {
            viewModel.loadLists()
            viewModel.reviewList.observe(viewLifecycleOwner, EventObserver {
                title = it.title
                createdDate = it.createdDate
                vocabularies.addAll(it.vocabularies)
                isLoadList = true

                loadReview()
            })
        } else {
            loadReview()
        }
    }

    private fun setButtonClickListener() {
        binding.btnYes.setOnClickListener {
            if (vocabularies.isNotEmpty()) {
                vocabularies.removeFirst()
                updateVocabularyText()
            }
        }

        binding.btnNo.setOnClickListener {
            if (vocabularies.isNotEmpty()) {
                val currentVocabulary = vocabularies.first()
                vocabularies.add(currentVocabulary)
                vocabularies.removeFirst()

                val action = FlipFrontFragmentDirections.actionGlobalBack(currentVocabulary)
                findNavController().navigate(action)
            }
        }
    }

    private fun loadReview() {
        setTitleAndDate()
        updateVocabularyText()
        enableButton()
    }

    private fun updateVocabularyText() {
        if (vocabularies.isNotEmpty()) {
            binding.tvVocabulary.text = vocabularies.first().word
        } else {
            viewModel.finishReview()
            vocabularies.clear()
            isLoadList = false
            title = ""
            createdDate = ""
        }
    }

    private fun setTitleAndDate() {
        if (title != "" && createdDate != "") {
            binding.tvListTitle.text = title
            binding.tvListCreatedDate.text = calculatePassedDate(createdDate)
        }
    }

    private fun calculatePassedDate(creationDate: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val createdDate = dateFormat.parse(creationDate)

        if (createdDate != null) {
            val diff = createdDate.time - currentDate.time
            val passedDays = (diff / (24 * 60 * 60 * 1000)).toInt().absoluteValue
            return getString(R.string.passedDays, passedDays)
        } else {
            throw IllegalArgumentException(getString(R.string.invalid_date_format))
        }
    }

    private fun enableButton() {
        binding.btnYes.isEnabled = true
        binding.btnNo.isEnabled = true
    }
}
