package com.example.otchelper.companies

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.atitto.mviflowarch.base.BaseNavigator
import com.atitto.mviflowarch.extensions.animateEnter
import com.atitto.mviflowarch.extensions.animateExit
import com.example.domain.companies.model.DomainStock
import com.example.otchelper.R
import com.example.otchelper.details.CompanyDetailsFragment

abstract class CompaniesNavigator : BaseNavigator(R.id.main_fragments_container) {

    abstract fun goDetails(stock: DomainStock, fragmentManager: FragmentManager)
}

class CompaniesNavigatorImpl : CompaniesNavigator() {

    override fun goDetails(stock: DomainStock, fragmentManager: FragmentManager) {
        fragmentManager.goWithAnimationAndBack(CompanyDetailsFragment.newInstance(stock))
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