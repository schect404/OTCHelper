package com.atittoapps.otchelper.base

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.atittoapps.otchelper.MainApplication
import com.atittoapps.otchelper.R

class SwipeToDeleteCallback(
    private val onDeleteCallback: (Int) -> Unit,
    private val textMessage: String
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START) {

    private var background = ColorDrawable(MainApplication.resolveColorFromAttr(R.attr.colorAlert))

    override fun onMove(
        p0: RecyclerView,
        p1: RecyclerView.ViewHolder,
        p2: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, p1: Int) {
        val position = viewHolder.adapterPosition
        try {
            onDeleteCallback.invoke(position)
        } catch (e: Exception) {

        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView

        if (dX < 0) {
            background.setBounds(
                (itemView.right + dX).toInt() - BACKGROUND_CORNER_OFFSET,
                itemView.top, itemView.right, itemView.bottom
            )
        } else {
            background.setBounds(0, 0, 0, 0)
        }

        background.draw(c)
        c.drawText(
            textMessage,
            itemView.right - (TEXT_SIZE / 2).times(textMessage.length) - TEXT_SIZE - 10,
            itemView.top + itemView.height / 2 + TEXT_SIZE / 4,
            Paint().apply {
                color = Color.WHITE
                textSize = TEXT_SIZE
            })
    }

    companion object {

        const val TEXT_SIZE = 42f
        const val BACKGROUND_CORNER_OFFSET = 20

    }
}