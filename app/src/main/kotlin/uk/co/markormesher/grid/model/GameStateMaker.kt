package uk.co.markormesher.grid.model

fun makeSampleGameState(size: Int = 5, totalCellStates: Int = 2): GameState {
	val state = GameState(size, totalCellStates)

	for (row in 0..(size - 1)) {
		for (col in 0..(size - 1)) {
			if (row != 0) {
				state.addLinkedNeighbour(row, col, GameState.Neighbour.NORTH)
			}
			if (col != size - 1) {
				state.addLinkedNeighbour(row, col, GameState.Neighbour.EAST)
			}
			if (row != size - 1) {
				state.addLinkedNeighbour(row, col, GameState.Neighbour.SOUTH)
			}
			if (col != 0) {
				state.addLinkedNeighbour(row, col, GameState.Neighbour.WEST)
			}
		}
	}

	return state
}
