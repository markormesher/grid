package uk.co.markormesher.grid.model

import android.os.Parcel
import android.os.Parcelable

class GameState(val size: Int, val totalCellStates: Int = 2): Parcelable {

	val cellStates: Array<Array<Int>> = Array(size) { Array(size) { 0 } }
	val cellLinkedNeighbours = Array(size) { Array(size) { 0 } }

	var userFlipCount = 0
	private var lastStatus = Status.NOT_STARTED
	var status = Status.NOT_STARTED
		set (value) {
			val oldValue = field
			field = value
			if (value != oldValue) {
                callOnStatusChangeListeners()
            }
			lastStatus = value
		}

	private val cellChangeListeners = HashSet<OnCellChangeListener>()
	private val statusChangeListeners = HashSet<OnStatusChangeListener>()

	fun addLinkedNeighbour(row: Int, col: Int, neighbour: Neighbour) {
		cellLinkedNeighbours[row][col] = cellLinkedNeighbours[row][col].or(neighbour.value)
	}

	fun flip(row: Int, col: Int, cascadeAction: Boolean = false, systemAction: Boolean = false) {
		if (status != Status.IN_PLAY && !systemAction) {
			return
		}

		if (!systemAction && !cascadeAction) {
			++userFlipCount
		}

		cellStates[row][col] = (cellStates[row][col] + 1).rem(totalCellStates)
		callOnCellListeners(row, col)

		if (!cascadeAction) {
			getNeighbourCoordinates(row, col).forEach { flip(it.first, it.second, true, systemAction) }

			if (isWinningState()) {
				status = Status.WON
			}
		}
	}

	private fun getNeighbourCoordinates(row: Int, col: Int): List<Pair<Int, Int>> {
		val linkedNeighbours = cellLinkedNeighbours[row][col]
		return Neighbour.ALL
				.filter { neighbour -> linkedNeighbours.and(neighbour.value) > 0 }
				.map { neighbour -> Pair(row + neighbour.rowDiff, col + neighbour.colDiff) }
				.filter { (x, y) -> x in 0..(size - 1) && y in 0..(size - 1) }
	}

	fun qtyWinningCells(): Int {
		return cellStates.sumBy { row -> row.count { state -> state == 0 } }
	}

	private fun isWinningState(): Boolean {
		return qtyWinningCells() == size * size
	}

	fun addOnCellChangeListener(listener: OnCellChangeListener) {
		cellChangeListeners.add(listener)
	}

	private fun callOnCellListeners(row: Int, col: Int) {
		cellChangeListeners.forEach { it.onCellChange(row, col) }
	}

	fun addOnStatusChangeListener(listener: OnStatusChangeListener) {
		statusChangeListeners.add(listener)
	}

	fun removeOnStatusChangeListener(listener: OnStatusChangeListener) {
		statusChangeListeners.remove(listener)
	}

	private fun callOnStatusChangeListeners() {
		statusChangeListeners.forEach { it.onStatusChange() }
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

	enum class Status(val value: Int) {
		NOT_STARTED(0),
		IN_PLAY(1),
		PAUSED(2),
		WON(3);

		companion object {
			fun fromValue(value: Int): Status = when (value) {
				0 -> NOT_STARTED
				1 -> IN_PLAY
				2 -> PAUSED
				3 -> WON
				else -> throw IllegalArgumentException("Could not get Status value of $value")
			}
		}
	}

	interface OnCellChangeListener {
		fun onCellChange(row: Int, col: Int)
	}

	interface OnStatusChangeListener {
		fun onStatusChange()
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(size)
		parcel.writeInt(totalCellStates)
		parcel.writeIntArray(cellStates.flatten().toIntArray())
		parcel.writeIntArray(cellLinkedNeighbours.flatten().toIntArray())
		parcel.writeInt(userFlipCount)
		parcel.writeInt(status.value)
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

			state.userFlipCount = parcel.readInt()
			state.status = Status.fromValue(parcel.readInt())

			return state
		}

		override fun newArray(size: Int): Array<GameState?> {
			return arrayOfNulls(size)
		}
	}

}
