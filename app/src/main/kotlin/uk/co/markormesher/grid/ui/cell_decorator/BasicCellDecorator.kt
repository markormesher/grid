package uk.co.markormesher.grid.ui.cell_decorator

import android.graphics.Color
import kotlinx.android.synthetic.main.ui_game_cell.view.*
import uk.co.markormesher.grid.model.GameState
import uk.co.markormesher.grid.ui.GameCell

open class BasicCellDecorator {

	private val pausedColour = Color.GRAY

	private val basicColours = arrayOf(
			Color.parseColor("#64dd17"),
			Color.parseColor("#d50000"),
			Color.parseColor("#2962ff")
	)

	open fun decorateCell(cell: GameCell, state: Int, gameState: GameState) {
		if (gameState.status == GameState.Status.PAUSED) {
			cell.inner.setBackgroundColor(pausedColour)
			cell.showLinkedNeighbours = false
		} else {
			cell.inner.setBackgroundColor(basicColours[state.rem(basicColours.size)])
			cell.showLinkedNeighbours = true
		}
	}

}
