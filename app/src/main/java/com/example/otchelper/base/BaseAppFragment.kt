package com.example.otchelper.base

import com.atitto.mviflowarch.base.BaseFragment
import com.atitto.mviflowarch.base.BaseModelIntent
import com.atitto.mviflowarch.base.BasePartialChange
import com.atitto.mviflowarch.base.BaseViewIntent
import com.atitto.mviflowarch.base.BaseViewState

abstract class BaseAppFragment<VI : BaseViewIntent,
        SI : BaseModelIntent, S : BaseViewState,
        PC : BasePartialChange<S>> : BaseFragment<VI, SI, S, PC>() {

    override fun handleProgress(isProgressFlowing: Boolean) {
        if (isProgressFlowing) ProgressFragmentDialog.showDialog(childFragmentManager)
        else ProgressFragmentDialog.hide(childFragmentManager)
    }

}