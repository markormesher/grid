package uk.co.markormesher.grid.model

import android.os.Parcel
import android.os.Parcelable

class GameState(val size: Int, val totalCellStates: Int = 2): Parcelable {

	val cellStates: Array<Array<Int>> = Array(size) { Array(size) { 0 } }
	val cellLinkedNeighbours = Array(size) { Array(size) { 0 } }

	var paused = false
		set(value) {
			field = value
			callOnStateListeners()
		}

	var userFlipCount = 0

	private val cellChangeListeners = HashSet<OnCellChangeListener>()
	private val stateChangeListeners = HashSet<OnStateChangeListener>()

	fun addLinkedNeighbour(row: Int, col: Int, neighbour: Neighbour) {
		cellLinkedNeighbours[row][col] = cellLinkedNeighbours[row][col].or(neighbour.value)
	}

	fun flip(row: Int, col: Int, cascadeAction: Boolean = false, systemAction: Boolean = false) {
		if (paused && !systemAction) {
			return
		}

		if (!systemAction && !cascadeAction) {
			++userFlipCount
		}

		cellStates[row][col] = (cellStates[row][col] + 1).rem(totalCellStates)
		callOnCellListeners(row, col)

		if (!cascadeAction) {
			getNeighbourCoordinates(row, col).forEach { flip(it.first, it.second, true, systemAction) }
			callOnStateListeners()
		}
	}

	private fun getNeighbourCoordinates(row: Int, col: Int): List<Pair<Int, Int>> {
		val linkedNeighbours = cellLinkedNeighbours[row][col]
		val output = ArrayList<Pair<Int, Int>>()
		Neighbour.ALL
				.filter { neighbour -> linkedNeighbours.and(neighbour.value) > 0 }
				.forEach { neighbour -> output.add(Pair(row + neighbour.rowDiff, col + neighbour.colDiff)) }
		return output
	}

	fun qtyWinningCells(): Int {
		return cellStates.sumBy { row -> row.count { state -> state == 0 } }
	}

	fun isWinningState(): Boolean {
		return qtyWinningCells() == size * size
	}

	fun addOnCellChangeListener(listener: OnCellChangeListener) {
		cellChangeListeners.add(listener)
	}

	private fun callOnCellListeners(row: Int, col: Int) {
		cellChangeListeners.forEach { it.onCellChange(row, col) }
	}

	fun addOnStateChangeListener(listener: OnStateChangeListener) {
		stateChangeListeners.add(listener)
	}

	fun removeOnStateChangeListener(listener: OnStateChangeListener) {
		stateChangeListeners.remove(listener)
	}

	private fun callOnStateListeners() {
		stateChangeListeners.forEach { it.onStateChange() }
	}

	enum class Neighbour(val value: Int, val rowDiff: Int, val colDiff: Int) {
		NORTH(1, -1, 0), NORTH_EAST(2, -1, 1),
		EAST(4, 0, 1), SOUTH_EAST(8, 1, 1),
		SOUTH(16, 1, 0), SOUTH_WEST(32, 1, -1),
		WEST(64, 0, -1), NORTH_WEST(128, -1, -1);

		companion object {
			val ALL = arrayOf(NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST)
		}
	}

	interface OnCellChangeListener {
		fun onCellChange(row: Int, col: Int)
	}

	interface OnStateChangeListener {
		fun onStateChange()
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(size)
		parcel.writeInt(totalCellStates)
		parcel.writeIntArray(cellStates.flatten().toIntArray())
		parcel.writeIntArray(cellLinkedNeighbours.flatten().toIntArray())
		parcel.writeInt(if (paused) 1 else 0)
	}

	override fun describeContents(): Int = 0

	companion object CREATOR: Parcelable.Creator<GameState> {
		override fun createFromParcel(parcel: Parcel): GameState {
			val size = parcel.readInt()
			val totalCellStates = parcel.readInt()

			val flattenedCellStates = IntArray(size * size, { 0 })
			parcel.readIntArray(flattenedCellStates)

			val flattenedCellLinkedNeighbours = IntArray(size * size, { 0 })
			parcel.readIntArray(flattenedCellLinkedNeighbours)

			val state = GameState(size, totalCellStates)
			for (row in 0..(size - 1)) {
				for (col in 0..(size - 1)) {
					val flatIndex = (row * size) + col
					state.cellStates[row][col] = flattenedCellStates[flatIndex]
					state.cellLinkedNeighbours[row][col] = flattenedCellLinkedNeighbours[flatIndex]
				}
			}

			val paused = parcel.readInt() == 1
			state.paused = paused

			return state
		}

		override fun newArray(size: Int): Array<GameState?> {
			return arrayOfNulls(size)
		}
	}

}
