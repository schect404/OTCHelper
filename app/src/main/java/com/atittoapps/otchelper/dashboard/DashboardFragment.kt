package com.atittoapps.otchelper.dashboard

import android.os.Bundle
import android.view.View
import com.atitto.mviflowarch.stub.SimpleFragment
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.databinding.FragmentDashboardBinding
import com.atittoapps.otchelper.viewBinding

class DashboardFragment : SimpleFragment() {

    private val binding by viewBinding(FragmentDashboardBinding::bind)

    override val layoutRes = R.layout.fragment_dashboard

    override val navigator = DashboardNavigator()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bnvNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.list -> navigator.stocks(childFragmentManager)
                R.id.watchlist -> navigator.watchList(childFragmentManager)
                R.id.settings -> navigator.settings(childFragmentManager)
                R.id.search -> navigator.search(childFragmentManager)
            }
            true
        }
        binding.bnvNavigation.selectedItemId = R.id.list
    }

    companion object {
        fun newInstance() = DashboardFragment()
    }
}