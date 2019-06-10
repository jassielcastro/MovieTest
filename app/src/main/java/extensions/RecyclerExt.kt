package extensions

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setLayoutHorizontal() {
    this.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
}

fun RecyclerView.setGridLayout(rows: Int) {
    this.layoutManager = GridLayoutManager(context, rows, RecyclerView.HORIZONTAL, false)
}