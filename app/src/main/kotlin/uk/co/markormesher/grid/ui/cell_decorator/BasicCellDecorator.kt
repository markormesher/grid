package uk.co.markormesher.grid.ui.cell_decorator

import android.graphics.Color
import kotlinx.android.synthetic.main.ui_game_cell.view.*
import uk.co.markormesher.grid.ui.GameCell

open class BasicCellDecorator {

	private val basicColours = arrayOf(
			Color.GREEN,
			Color.RED,
			Color.BLUE
	)

	open fun decorateCell(cell: GameCell, state: Int, totalStates: Int) {
		cell.inner.setBackgroundColor(basicColours[state.rem(basicColours.size)])
	}

}
