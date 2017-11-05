package uk.co.markormesher.grid.model

import android.os.Parcel
import android.os.Parcelable

class GameStateMaker(
		val size: Int = 2,
		val qtyCellStates: Int = 2,
		val defaultNeighbours: Int = NeighbourSets.NONE
): Parcelable {

	fun makeGameState(): GameState {
		val state = GameState(size, qtyCellStates)

		val defaultNeighboursAsArray = GameState.Neighbour.arrayFromValue(defaultNeighbours)
		for (row in 0 until size) {
			for (col in 0 until size) {
				defaultNeighboursAsArray
						.filter { it.isAllowed(size, row, col) }
						.forEach { state.addLinkedNeighbour(row, col, it) }
			}
		}

		return state
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(size)
		parcel.writeInt(qtyCellStates)
		parcel.writeInt(defaultNeighbours)
	}

	override fun describeContents() = 0

	companion object CREATOR: Parcelable.Creator<GameStateMaker> {
		override fun createFromParcel(parcel: Parcel): GameStateMaker {
			return GameStateMaker(parcel.readInt(), parcel.readInt(), parcel.readInt())
		}

		override fun newArray(size: Int): Array<GameStateMaker?> {
			return arrayOfNulls(size)
		}
	}
}

object NeighbourSets {
	val NONE = 0

	val VERTICAL = GameState.Neighbour.EAST.value
			.or(GameState.Neighbour.WEST.value)

	val HORIZONTAL = GameState.Neighbour.NORTH.value
			.or(GameState.Neighbour.SOUTH.value)

	val ADJACENT = GameState.Neighbour.NORTH.value
			.or(GameState.Neighbour.EAST.value)
			.or(GameState.Neighbour.SOUTH.value)
			.or(GameState.Neighbour.WEST.value)

	val DIAGONAL = GameState.Neighbour.NORTH_EAST.value
			.or(GameState.Neighbour.SOUTH_EAST.value)
			.or(GameState.Neighbour.SOUTH_WEST.value)
			.or(GameState.Neighbour.NORTH_WEST.value)
}
