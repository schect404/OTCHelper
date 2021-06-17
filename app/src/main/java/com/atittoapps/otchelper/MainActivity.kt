package com.atittoapps.otchelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.atittoapps.otchelper.dashboard.DashboardFragment
import com.atittoapps.otchelper.worker.WorkManagerScheduler
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