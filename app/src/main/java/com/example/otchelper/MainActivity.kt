package com.example.otchelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.otchelper.companies.CompaniesFragment
import com.example.otchelper.dashboard.DashboardFragment
import com.example.otchelper.worker.WorkManagerScheduler
import org.koin.core.component.KoinApiExtension

class MainActivity : AppCompatActivity() {
    @KoinApiExtension
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragments_container, DashboardFragment.newInstance())
                .commit()
        }
        WorkManagerScheduler.refreshPeriodicWork(this)
    }
}