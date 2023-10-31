package com.gurumlab.vocaroutine

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gurumlab.vocaroutine.databinding.ItemDetailListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailListAdapter :
    ListAdapter<Vocabulary, DetailListAdapter.DetailListViewHolder>(VocabularyDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailListViewHolder {
        return DetailListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DetailListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DetailListViewHolder(private val binding: ItemDetailListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val gptApiClient =
            VocaRoutineApplication.appContainer.provideGptApiClient(BuildConfig.GPT_API_KEY)

        fun bind(item: Vocabulary) {
            val meaningList = item.meaning.split(",")
            val meaning = meaningList.mapIndexed { index, value ->
                "${index + 1}. ${value.trim()}"
            }.joinToString("\n")

            binding.tvVocabulary.text = item.word
            binding.tvMeaning.text = meaning

            //CoroutineScope에 대한 대안은 MVVM 패턴으로 전환시 구현
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = gptApiClient.getResponse(
                        ChatRequest(
                            "gpt-3.5-turbo", listOf(
                                ChatMessage("system", "${item.word}에 대한 어원을 알려줘. 30자로 설명을 제한해줘.")
                            )
                        )
                    )
                    withContext(Dispatchers.Main) {
                        binding.tvEtymologyDescription.text =
                            request.choices.first().message.content
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.tvEtymologyDescription.text =
                            binding.root.context.getString(R.string.gpt_response_error)
                    }
                }
            }
        }

        companion object {

            fun from(parent: ViewGroup): DetailListViewHolder {
                return DetailListViewHolder(
                    ItemDetailListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}