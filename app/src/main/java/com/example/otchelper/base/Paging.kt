package com.example.otchelper.base

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.atomic.AtomicReference

typealias PagingAdapter = () -> Unit

class PagingListener(private val pageSize: Int = 10) : RecyclerView.OnScrollListener() {

    private val isLoading = AtomicReference(false)

    private val isRegistered = AtomicReference(false)

    private val listCallback = PagingListCallback(isLoading)

    private val pageListener = AtomicReference<PagingAdapter>()

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(view, dx, dy)

        if (isRegistered.getAndSet(true).not()) view?.adapter?.registerAdapterDataObserver(listCallback)

        if (isLoading.get() || view?.isComputingLayout == true) return

        val layoutManager = view?.layoutManager ?: return

        val itemCount = layoutManager.itemCount
        val lastVisiblePosition = (layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: (layoutManager as? GridLayoutManager)?.findLastVisibleItemPosition() ?: throw IllegalArgumentException("Paging not supported for ${view.layoutManager}")
        val offset = lastVisiblePosition.plus(layoutManager.offset())

        if (offset > itemCount && itemCount % pageSize >= 0) {
            onNext()
        }
    }

    private fun RecyclerView.LayoutManager.offset(): Int {
        return (this as? GridLayoutManager)?.spanCount?.times(2) ?: 5
    }

    private fun onNext() {
        isLoading.set(true)
        pageListener.get()?.invoke()
    }

    fun attach(pageListener: PagingAdapter): PagingListener {
        this.pageListener.set(pageListener)
        return this
    }
}
class PagingListCallback(private val trigger: AtomicReference<Boolean>): RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        super.onChanged()
        trigger.set(false)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        super.onItemRangeChanged(positionStart, itemCount)
        trigger.set(false)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        super.onItemRangeChanged(positionStart, itemCount, payload)
        trigger.set(false)
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        trigger.set(false)
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount)
        trigger.set(false)
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        super.onItemRangeRemoved(positionStart, itemCount)
        trigger.set(false)
    }
}

fun RecyclerView.paging(listener: PagingAdapter) = addOnScrollListener(PagingListener().attach { listener() })