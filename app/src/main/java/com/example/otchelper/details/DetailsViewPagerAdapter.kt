package com.example.otchelper.details

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.domain.companies.model.DomainStock
import com.example.otchelper.R
import com.example.otchelper.details.links.LinksFragment
import com.example.otchelper.details.news.CompanyNewsFragment
import com.example.otchelper.details.profile.CompanyProfileFragment
import com.example.otchelper.details.reports.CompanyReportsFragment
import com.example.otchelper.details.security.CompanySecurityDetailsFragment

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
