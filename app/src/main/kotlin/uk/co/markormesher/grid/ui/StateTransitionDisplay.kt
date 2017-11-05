package uk.co.markormesher.grid.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import uk.co.markormesher.grid.R
import uk.co.markormesher.grid.model.GameState
import uk.co.markormesher.grid.ui.cell_decorator.BasicCellDecorator

class StateTransitionDisplay @JvmOverloads constructor(context: Context, private val attrs: AttributeSet? = null, private val defStyle: Int = 0):
		LinearLayout(context, attrs, defStyle) {

	private val cellViews = ArrayList<GameCell>()

	var gameState = GameState(0)
		set(state) {
			field = state
			generateCells()
		}

	var decorator = BasicCellDecorator()
		set(value) {
			field = value
			decorateCells()
		}

	private fun generateCells() {
		cellViews.forEach { removeView(it) }
		cellViews.clear()

		for (i in 0 until gameState.totalCellStates) {
			val child = GameCell(context)
			cellViews.add(child)
			child.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
			addView(child)

			if (i != gameState.totalCellStates - 1) {
				val arrow = ImageView(context)
				arrow.setImageResource(R.drawable.ic_navigate_next_white_48dp)
				arrow.adjustViewBounds = true
				arrow.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
				addView(arrow)
			}
		}
	}

	private fun decorateCells() {
		cellViews.forEachIndexed { index, cell ->
			val position = if (index == gameState.totalCellStates - 1) 0 else index + 1
			decorator.decorateCell(cell, position, gameState, demoMode = true)
		}
	}
}
