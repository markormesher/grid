package uk.co.markormesher.grid.model

object NeighbourSets {
	val NONE = emptyArray<GameState.Neighbour>()

	val VERTICAL = arrayOf(
			GameState.Neighbour.EAST,
			GameState.Neighbour.WEST
	)

	val HORIZONTAL = arrayOf(
			GameState.Neighbour.NORTH,
			GameState.Neighbour.SOUTH
	)

	val ADJACENT = arrayOf(
			GameState.Neighbour.NORTH,
			GameState.Neighbour.EAST,
			GameState.Neighbour.SOUTH,
			GameState.Neighbour.WEST
	)

	val DIAGONAL = arrayOf(
			GameState.Neighbour.NORTH_EAST,
			GameState.Neighbour.SOUTH_EAST,
			GameState.Neighbour.SOUTH_WEST,
			GameState.Neighbour.NORTH_WEST
	)
}

fun makeSimpleGameState(size: Int = 5, qtyCellStates: Int = 2, neighbours: Array<GameState.Neighbour> = NeighbourSets.NONE): GameState {
	val state = GameState(size, qtyCellStates)

	for (row in 0..(size - 1)) {
		for (col in 0..(size - 1)) {
			neighbours
					.filter { it.isAllowed(size, row, col) }
					.forEach { state.addLinkedNeighbour(row, col, it) }
		}
	}

	return state
}
