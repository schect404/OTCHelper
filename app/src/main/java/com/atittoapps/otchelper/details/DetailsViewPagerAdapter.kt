package com.atittoapps.otchelper.details

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.details.levels.LevelsFragment
import com.atittoapps.otchelper.details.links.LinksFragment
import com.atittoapps.otchelper.details.news.CompanyNewsFragment
import com.atittoapps.otchelper.details.profile.CompanyProfileFragment
import com.atittoapps.otchelper.details.reports.CompanyReportsFragment
import com.atittoapps.otchelper.details.security.CompanySecurityDetailsFragment

class DetailsViewPagerAdapter(
    private val context: Context,
    fragmentManager: FragmentManager,
    private val stock: DomainStock
) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount() = 5

    override fun getItem(position: Int) = when (position) {
        0 -> CompanyProfileFragment.newInstance(stock)
        1 -> CompanySecurityDetailsFragment.newInstance(stock)
        2 -> CompanyNewsFragment.newInstance(stock)
        3 -> CompanyReportsFragment.newInstance(stock)
        else -> LinksFragment.newInstance(stock)
    }

    override fun getPageTitle(position: Int) = when(position) {
        0 -> context.getString(R.string.profile)
        1 -> context.getString(R.string.security)
        2 -> context.getString(R.string.news)
        3 -> context.getString(R.string.reports)
        else -> context.getString(R.string.links)
    }
}
