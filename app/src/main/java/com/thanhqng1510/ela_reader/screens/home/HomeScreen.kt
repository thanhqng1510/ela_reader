package com.thanhqng1510.ela_reader.screens.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import com.thanhqng1510.ela_reader.R
import com.thanhqng1510.ela_reader.databinding.HomeScreenBinding
import com.thanhqng1510.ela_reader.screens.AppViewModel
import com.thanhqng1510.ela_reader.utils.fragment_utils.BaseFragment
import com.thanhqng1510.ela_reader.utils.fragment_utils.RefreshableBaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO: There's a bug about status icon
// TODO: Show scrollbar
// TODO: Bottom nav bar not show
@AndroidEntryPoint
class HomeScreen : BaseFragment() {
    // This property is only valid between onCreateView and onDestroyView
    private var bindings: HomeScreenBinding? = null

    private val viewModel: HomeViewModel by viewModels()

    private val appViewModel: AppViewModel by activityViewModels()

    private val addedFragments = mutableMapOf<HomeTabType, RefreshableBaseFragment>()

    override fun setupView(inflater: LayoutInflater, container: ViewGroup?): View {
        bindings = HomeScreenBinding.inflate(inflater, container, false)
        return bindings!!.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.app_bar, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_book_btn -> {
                findNavController().navigate(HomeScreenDirections.actionHomeScreenToAddBookScreen())
                true
            }
            R.id.settings_btn -> {
                findNavController().navigate(HomeScreenDirections.actionHomeScreenToSettingsScreen())
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.currentTabType?.let {
            addedFragments[it] =
                childFragmentManager.findFragmentByTag(it.getTag()) as RefreshableBaseFragment
        } ?: run {
            bindings!!.bottomNavigation.selectedItemId =
                HomeTabType.LIBRARY.menuItemId // Need to set here so our listener will be called
        }
    }

    override fun setupBindings() {
        setHasOptionsMenu(true)

        appViewModel.appBarTitle.value =
            requireContext().resources.getString(R.string.home_screen_label)
    }

    override fun setupCollectors() {}

    override fun setupListeners() {
        bindings!!.refreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                whenStarted {
                    addedFragments[viewModel.currentTabType]?.refresh()
                    bindings!!.refreshLayout.isRefreshing = false
                }
            }
        }
        bindings!!.bottomNavigation.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener when (item.itemId) {
                HomeTabType.LIBRARY.menuItemId -> {
                    setCurrentPage(HomeTabType.LIBRARY)
                    true
                }
                HomeTabType.BOOKMARKS.menuItemId -> {
                    setCurrentPage(HomeTabType.BOOKMARKS)
                    true
                }
                else -> false
            }
        }
    }

    override fun cleanUpView() {
        bindings = null
    }

    private fun setCurrentPage(type: HomeTabType) {
        val fragment = addedFragments[type] ?: type.getFragment()
        val prevFragmentType = viewModel.currentTabType
        viewModel.currentTabType = type

        childFragmentManager.commit {
            setReorderingAllowed(true)
            prevFragmentType?.run { hide(addedFragments[this] ?: throw IllegalArgumentException()) }

            if (!addedFragments.containsKey(type)) {
                add(bindings!!.tabFragment.id, fragment, type.getTag())
                addedFragments[type] = fragment
            } else
                show(fragment)
        }
    }
}