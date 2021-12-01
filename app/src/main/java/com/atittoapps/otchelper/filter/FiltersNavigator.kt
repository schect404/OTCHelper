package com.atittoapps.otchelper.filter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.atitto.mviflowarch.base.BaseNavigator
import com.atitto.mviflowarch.extensions.animateEnter
import com.atitto.mviflowarch.extensions.animateExit
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.domain.companies.model.Industry
import com.atittoapps.domain.companies.model.Market
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.filter.industries.IndustriesFragment
import com.atittoapps.otchelper.filter.markets.MarketsFragment
import com.atittoapps.otchelper.filter.sorting.AppSortingBy
import com.atittoapps.otchelper.filter.sorting.SortingFragment

abstract class FiltersNavigator : BaseNavigator(R.id.main_fragments_container) {
    abstract fun goSorting(nowChosen: AppSortingBy, fragmentManager: FragmentManager)

    abstract fun goIndustries(nowChosen: List<Industry>, fragmentManager: FragmentManager)

    abstract fun goMarkets(nowChosen: List<Market>, fragmentManager: FragmentManager)
}

class FiltersNavigatorImpl : FiltersNavigator() {

    override fun goSorting(nowChosen: AppSortingBy, fragmentManager: FragmentManager) {
        fragmentManager.goWithAnimationAndBack(SortingFragment.newInstance(nowChosen))
    }

    override fun goIndustries(nowChosen: List<Industry>, fragmentManager: FragmentManager) {
        fragmentManager.goWithAnimationAndBack(IndustriesFragment.newInstance(nowChosen))
    }

    override fun goMarkets(nowChosen: List<Market>, fragmentManager: FragmentManager) {
        fragmentManager.goWithAnimationAndBack(MarketsFragment.newInstance(nowChosen))
    }

    private fun FragmentManager.goWithAnimationAndBack(targetFragment: Fragment, vararg sharedViews: View, idContainer: Int? = null) {

        val previousFragment = findFragmentById(R.id.main_fragments_container)
        previousFragment?.animateExit()

        //targetFragment.animateShared(previousFragment.requireContext())
        targetFragment.animateEnter()

        beginTransaction()
                .add(R.id.main_fragments_container, targetFragment)
                .apply { sharedViews.forEach { addSharedElement(it, it.transitionName) } }
                .addToBackStack(targetFragment::class.java.name)
                .commit()

    }

}