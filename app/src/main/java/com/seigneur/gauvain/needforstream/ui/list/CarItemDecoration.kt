package com.seigneur.gauvain.needforstream.ui.list

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

//Simple item decoration for each item of the car list
class CarItemDecoration(private val spaceTopBottom: Int,
                        private val spaceBetween: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = spaceTopBottom
            }
            left =  spaceBetween
            right = spaceBetween
            bottom = spaceTopBottom
        }
    }
}