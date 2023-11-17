package com.gurumlab.vocaroutine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.gurumlab.vocaroutine.databinding.FragmentDetailListBinding


class DetailListFragment : BaseFragment<FragmentDetailListBinding>() {

    private val args: DetailListFragmentArgs by navArgs()
    private lateinit var list: MyList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = args.list
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailListBinding {
        return FragmentDetailListBinding.inflate(inflater, container, false)
    }
}