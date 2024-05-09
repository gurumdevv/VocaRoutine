package com.gurumlab.vocaroutine.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.ui.common.ListClickListener
import com.gurumlab.vocaroutine.ui.common.ListDiffUtil
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.databinding.ItemMyListBinding
import com.gurumlab.vocaroutine.ui.common.ItemTouchHelperListener

class MyListAdapter(
    private val viewModel: MyListViewModel,
    private val clickListener: ListClickListener
) : ListAdapter<ListInfo, MyListAdapter.MyListViewHolder>(ListDiffUtil()), ItemTouchHelperListener {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyListViewHolder {
        return MyListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class MyListViewHolder(private val binding: ItemMyListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(list: ListInfo, clickListener: ListClickListener) {
            binding.list = list
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): MyListViewHolder {
                return MyListViewHolder(
                    ItemMyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean = false

    override fun onItemSwipe(position: Int) {}

    override fun onRightClick(position: Int, viewHolder: RecyclerView.ViewHolder?) {
        viewModel.deleteList(getItem(position))
        val newList = this.currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
        viewModel.setSnackbarMessage(R.string.delete_list_complete)
    }
}