package com.atittoapps.otchelper.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.atitto.mviflowarch.base.BaseNavigator
import com.atitto.mviflowarch.extensions.animateExit
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.companies.CompaniesFragment
import com.atittoapps.otchelper.filter.FiltersFragment
import com.atittoapps.otchelper.search.SearchFragment
import com.atittoapps.otchelper.watchlist.WatchlistFragment

class DashboardNavigator : BaseNavigator(R.id.main_fragments_container) {

    fun stocks(fragmentManager: FragmentManager) {
        fragmentManager.go(CompaniesFragment.newInstance())
    }

    fun watchList(fragmentManager: FragmentManager) {
        fragmentManager.go(WatchlistFragment.newInstance())
    }

    fun settings(fragmentManager: FragmentManager) {
        fragmentManager.go(FiltersFragment.newInstance())
    }

    fun search(fragmentManager: FragmentManager) {
        fragmentManager.go(SearchFragment.newInstance())
    }

    private inline fun <reified T : Fragment> FragmentManager.go(targetFragment: T) {
        popBackStack(null, 0)

        findFragmentById(R.id.vFragmentContainer)?.let {
            it.animateExit()
        }

        beginTransaction()
            .replace(R.id.vFragmentContainer, targetFragment)
            .commit()
    }

}