package uk.co.markormesher.grid.model

import java.util.*

val random by lazy { Random() }

fun makeSampleGameState(size: Int = 5): GameState {
	val state = GameState(size, 3)

	for (row in 0..(size - 1)) {
		for (col in 0..(size - 1)) {
			state.resetLinkedNeighbours(row, col)
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

	for (i in 0..size) {
		state.flip(random.nextInt(size), random.nextInt(size))
	}

	return state
}
