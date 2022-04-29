package com.thanhqng1510.bookreadingapp_android.utils.fragment_utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(), EasyFragment {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = setupView(inflater, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBindings(savedInstanceState)
        setupCollectors()
        setupListeners()
    }

    override fun onDestroyView() {
        cleanUpView()
        super.onDestroyView()
    }
}