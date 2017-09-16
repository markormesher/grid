package uk.co.markormesher.grid.model

class GameState(val size: Int, val totalCellStates: Int = 2) {

	val cellStates: Array<Array<Int>> = Array(size) { Array(size) { 0 } }
	val cellLinkedNeighbours = Array(size) { Array(size) { 0 } }

	private val cellChangeListeners = HashSet<OnCellChangeListener>()

	fun setLinkedNeighbours(row: Int, col: Int, vararg neighbours: Neighbour) {
		resetLinkedNeighbours(row, col)
		neighbours.forEach { addLinkedNeighbour(row, col, it) }
	}

	fun resetLinkedNeighbours(row: Int, col: Int) {
		cellLinkedNeighbours[row][col] = 0
	}

	fun addLinkedNeighbour(row: Int, col: Int, neighbour: Neighbour) {
		cellLinkedNeighbours[row][col] = cellLinkedNeighbours[row][col].or(neighbour.value)
	}

	fun flip(row: Int, col: Int, cascadeAction: Boolean = false) {
		cellStates[row][col] = (cellStates[row][col] + 1).rem(totalCellStates)
		callOnCellListeners(row, col)

		if (!cascadeAction) {
			getNeighbourCoordinates(row, col).forEach { flip(it.first, it.second, true) }
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

	fun isWinningState(): Boolean {
		return cellStates.all { row -> row.all { state -> state == 0 } }
	}

	fun addOnCellChangeListener(listener: OnCellChangeListener) {
		cellChangeListeners.add(listener)
	}

	private fun callOnCellListeners(row: Int, col: Int) {
		cellChangeListeners.forEach { it.onCellChange(row, col) }
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

}
