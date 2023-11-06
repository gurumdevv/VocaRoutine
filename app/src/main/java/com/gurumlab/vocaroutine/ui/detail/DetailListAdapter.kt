package com.gurumlab.vocaroutine.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gurumlab.vocaroutine.BuildConfig
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.VocaRoutineApplication
import com.gurumlab.vocaroutine.ui.common.VocabularyDiffUtil
import com.gurumlab.vocaroutine.data.model.ChatMessage
import com.gurumlab.vocaroutine.data.model.ChatRequest
import com.gurumlab.vocaroutine.data.model.Vocabulary
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
            binding.vocabulary = item

            //업로드 기능 구현 부분으로 이동 예정
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