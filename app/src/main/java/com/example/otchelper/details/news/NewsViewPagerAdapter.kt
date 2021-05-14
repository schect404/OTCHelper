package com.example.otchelper.details.news

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.domain.companies.model.DomainStock
import com.example.otchelper.R
import com.example.otchelper.details.otcnews.OtcNewsFragment

class NewsViewPagerAdapter(
    private val context: Context,
    fragmentManager: FragmentManager,
    private val stock: DomainStock
) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount() = 2

    override fun getItem(position: Int) = when (position) {
        0 -> OtcNewsFragment.newInstanceNotExternal(stock)
        else -> OtcNewsFragment.newInstanceExternal(stock)
    }

    override fun getPageTitle(position: Int) = when(position) {
        0 -> context.getString(R.string.otc)
        else-> context.getString(R.string.external)
    }
}
