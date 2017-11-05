package uk.co.markormesher.grid.ui.cell_decorator

import android.graphics.Color
import android.view.View
import kotlinx.android.synthetic.main.ui_game_cell.view.*
import uk.co.markormesher.grid.R
import uk.co.markormesher.grid.model.GameState
import uk.co.markormesher.grid.ui.GameCell

open class BasicCellDecorator {

	private val pausedColour = Color.GRAY

	private val basicColours = arrayOf(
			Color.parseColor("#64dd17"),
			Color.parseColor("#d50000"),
			Color.parseColor("#2962ff")
	)

	open fun decorateCell(cell: GameCell, state: Int, gameState: GameState, demoMode: Boolean = false) {
		cell.icon.visibility = View.GONE

		if (gameState.status == GameState.Status.PAUSED && !demoMode) {
			cell.inner.setBackgroundColor(pausedColour)
			cell.showLinkedNeighbours = false
		} else {
			cell.inner.setBackgroundColor(basicColours[state.rem(basicColours.size)])
			cell.showLinkedNeighbours = true
			if (demoMode && state == 0) {
				cell.icon.visibility = View.VISIBLE
				cell.icon.setImageResource(R.drawable.ic_done_white_48dp)
			}
		}
	}

}
