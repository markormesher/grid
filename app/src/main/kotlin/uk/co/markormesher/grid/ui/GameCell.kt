package uk.co.markormesher.grid.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.ui_game_cell.view.*
import uk.co.markormesher.grid.R
import uk.co.markormesher.grid.model.GameState

class GameCell @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0)
	: LinearLayout(context, attrs, defStyle, defStyleRes) {

	init {
		View.inflate(context, R.layout.ui_game_cell, this)
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val size = Math.min(widthMeasureSpec, heightMeasureSpec)
		super.onMeasure(size, size)
	}

	var linkedNeighbours = 0
		set(value) {
			field = value
			onLinkedNeighboursUpdate()
		}

	private fun onLinkedNeighboursUpdate() {
		updateSingleNeighbour(north_neighbour_indicator, GameState.Neighbour.NORTH)
		updateSingleNeighbour(north_east_neighbour_indicator, GameState.Neighbour.NORTH_EAST)
		updateSingleNeighbour(east_neighbour_indicator, GameState.Neighbour.EAST)
		updateSingleNeighbour(south_east_neighbour_indicator, GameState.Neighbour.SOUTH_EAST)
		updateSingleNeighbour(south_neighbour_indicator, GameState.Neighbour.SOUTH)
		updateSingleNeighbour(south_west_neighbour_indicator, GameState.Neighbour.SOUTH_WEST)
		updateSingleNeighbour(west_neighbour_indicator, GameState.Neighbour.WEST)
		updateSingleNeighbour(north_west_neighbour_indicator, GameState.Neighbour.NORTH_WEST)
	}

	private fun updateSingleNeighbour(indicator: View, position: GameState.Neighbour) {
		indicator.visibility = if (linkedNeighbours.and(position.value) > 0) {
			View.VISIBLE
		} else {
			View.GONE
		}
	}

}
