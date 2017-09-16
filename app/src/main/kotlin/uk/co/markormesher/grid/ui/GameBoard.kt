package uk.co.markormesher.grid.ui

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import uk.co.markormesher.grid.model.GameState
import uk.co.markormesher.grid.ui.cell_decorator.BasicCellDecorator


class GameBoard @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
	: RecyclerView(context, attrs, defStyle) {

	var state = GameState(0)
		set(state) {
			field = state
			layoutManager = GridLayoutManager(context, state.size)
			state.addOnCellChangeListener(cellChangeListener)
			adapter.notifyDataSetChanged()
		}

	var decorator = BasicCellDecorator()
		set(value) {
			field = value
			adapter.notifyDataSetChanged()
		}

	private val cellChangeListener = object: GameState.OnCellChangeListener {
		override fun onCellChange(row: Int, col: Int) {
			val position = (row * state.size) + col
			adapter.notifyItemChanged(position)
		}
	}

	init {
		overScrollMode = View.OVER_SCROLL_NEVER
		adapter = GameCellAdapter()
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val size = Math.min(widthMeasureSpec, heightMeasureSpec)
		super.onMeasure(size, size)
	}

	inner class GameCellAdapter: RecyclerView.Adapter<GameCellHolder>() {

		override fun getItemCount(): Int = state.size * state.size

		override fun onBindViewHolder(holder: GameCellHolder, position: Int) {
			val cell = holder.cell
			val row = position / state.size
			val col = position.rem(state.size)

			val cellState = state.cellStates[row][col]
			if (holder.lastState != cellState) {
				decorator.decorateCell(cell, cellState, state.totalCellStates)
			}

			if (holder.assignedRow != row || holder.assignedCol != col) {
				holder.assignedRow = row
				holder.assignedCol = col

				cell.linkedNeighbours = state.cellLinkedNeighbours[row][col]
				cell.setOnClickListener { state.flip(row, col) }
			}
		}

		override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GameCellHolder {
			val cell = GameCell(context)
			cell.layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
			return GameCellHolder(cell)
		}
	}

	class GameCellHolder(val cell: GameCell): ViewHolder(cell) {
		var assignedRow = -1
		var assignedCol = -1
		var lastState = -1
	}

}
