package com.example.otchelper.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.atitto.mviflowarch.base.BaseNavigator
import com.atitto.mviflowarch.extensions.animateExit
import com.example.otchelper.R
import com.example.otchelper.companies.CompaniesFragment
import com.example.otchelper.filter.FiltersFragment
import com.example.otchelper.search.SearchFragment
import com.example.otchelper.watchlist.WatchlistFragment

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