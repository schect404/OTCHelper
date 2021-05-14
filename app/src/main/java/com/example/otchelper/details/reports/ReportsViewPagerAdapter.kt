package com.example.otchelper.details.reports

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.domain.companies.model.DomainStock
import com.example.otchelper.R
import com.example.otchelper.details.otcnews.OtcNewsFragment
import com.example.otchelper.details.reports.sec.ReportsFragment

class ReportsViewPagerAdapter(
    private val context: Context,
    fragmentManager: FragmentManager,
    private val stock: DomainStock
) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount() = 2

    override fun getItem(position: Int) = when (position) {
        0 -> ReportsFragment.newInstanceSec(stock)
        else -> ReportsFragment.newInstanceNotSec(stock)
    }

    override fun getPageTitle(position: Int) = when(position) {
        0 -> context.getString(R.string.sec)
        else -> context.getString(R.string.otc)
    }
}
