package com.example.otchelper.base

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.otchelper.R

class ProgressFragmentDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireActivity()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setContentView(R.layout.fragment_progressbar)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }

    companion object {

        fun showDialog(fragmentManager: FragmentManager) {
            showWhenNotOnScreen(ProgressFragmentDialog(), fragmentManager)
        }

        fun hide(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(ProgressFragmentDialog::class.java.name) as? ProgressFragmentDialog)?.dismiss()
        }

        private fun showWhenNotOnScreen(
            dialog: ProgressFragmentDialog,
            fragmentManager: FragmentManager
        ) {
            val dialogInStack =
                (fragmentManager.findFragmentByTag(ProgressFragmentDialog::class.java.name) as? ProgressFragmentDialog)
            if (dialogInStack == null) dialog.show(
                fragmentManager,
                ProgressFragmentDialog::class.java.name
            )
        }

    }

}