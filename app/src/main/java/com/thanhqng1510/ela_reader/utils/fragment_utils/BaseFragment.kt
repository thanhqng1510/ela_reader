package com.thanhqng1510.ela_reader.utils.fragment_utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thanhqng1510.ela_reader.utils.activity_utils.EasyActivity

abstract class BaseFragment : Fragment(), EasyFragment {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = setupView(inflater, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBindings()
        setupCollectors()
        setupListeners()
    }

    override fun onDestroyView() {
        cleanUpView()
        super.onDestroyView()
    }

    override fun showSnackbar(message: String) =
        (requireActivity() as EasyActivity).showSnackbar(message)

    // Job may return a result string that will be shown on snackbar after complete
    override fun waitJobShowProgressOverlayAsync(job: suspend () -> String?) =
        (requireActivity() as EasyActivity).waitJobShowProgressOverlayAsync(job)
}