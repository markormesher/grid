package uk.co.markormesher.grid.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import uk.co.markormesher.grid.R

class NeighbourIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0)
	: LinearLayout(context, attrs, defStyle, defStyleRes) {

	init {
		View.inflate(context, R.layout.ui_neighbour_indicator, this)
	}

}
