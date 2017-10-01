package uk.co.markormesher.grid.data

import uk.co.markormesher.grid.model.Level
import uk.co.markormesher.grid.model.NeighbourSets
import uk.co.markormesher.grid.model.makeSimpleGameState

object LevelHelper {

	val allLevels by lazy {
		val levels = ArrayList<Level>()

		// tutorial levels
		levels.add(Level(
				stage = 1, subStage = 1, flips = 2,
				initialState = makeSimpleGameState(size = 4, qtyCellStates = 2),
				helpTitle = "Tutorial 1/4",
				helpBody = "Some cells will be been flipped; tap them to flip them back."
		))
		levels.add(Level(
				stage = 1, subStage = 2, flips = 2,
				initialState = makeSimpleGameState(size = 4, qtyCellStates = 3),
				helpTitle = "Tutorial 2/4",
				helpBody = "These cells have three states; tap them twice to flip them back."
		))
		levels.add(Level(
				stage = 1, subStage = 3, flips = 2,
				initialState = makeSimpleGameState(size = 5, qtyCellStates = 2, neighbours = NeighbourSets.VERTICAL),
				helpTitle = "Tutorial 3/4",
				helpBody = "White dots indicate neighbours. When you flip a cell, its neighbours flip too."
		))
		levels.add(Level(
				stage = 1, subStage = 4, flips = 2,
				initialState = makeSimpleGameState(size = 5, qtyCellStates = 2, neighbours = NeighbourSets.HORIZONTAL),
				helpTitle = "Tutorial 4/4",
				helpBody = "Neighbours can be in different places; always check the dots."
		))

		// stage 2: get used to neighbours
		levels.add(Level(
				stage = 2, subStage = 1, flips = 2,
				initialState = makeSimpleGameState(size = 5, qtyCellStates = 2, neighbours = NeighbourSets.VERTICAL)
		))
		levels.add(Level(
				stage = 2, subStage = 2, flips = 3,
				initialState = makeSimpleGameState(size = 5, qtyCellStates = 2, neighbours = NeighbourSets.VERTICAL)
		))
		levels.add(Level(
				stage = 2, subStage = 3, flips = 2,
				initialState = makeSimpleGameState(size = 5, qtyCellStates = 2, neighbours = NeighbourSets.HORIZONTAL)
		))
		levels.add(Level(
				stage = 2, subStage = 4, flips = 3,
				initialState = makeSimpleGameState(size = 5, qtyCellStates = 2, neighbours = NeighbourSets.HORIZONTAL)
		))
		levels.add(Level(
				stage = 2, subStage = 5, flips = 1,
				initialState = makeSimpleGameState(size = 5, qtyCellStates = 2, neighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = 6, flips = 2,
				initialState = makeSimpleGameState(size = 5, qtyCellStates = 2, neighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = 7, flips = 3,
				initialState = makeSimpleGameState(size = 6, qtyCellStates = 2, neighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = 8, flips = 4,
				initialState = makeSimpleGameState(size = 6, qtyCellStates = 2, neighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = 9, flips = 5,
				initialState = makeSimpleGameState(size = 6, qtyCellStates = 2, neighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = 10, flips = 5,
				initialState = makeSimpleGameState(size = 6, qtyCellStates = 2, neighbours = NeighbourSets.ADJACENT)
		))

		return@lazy levels
	}

	private val levelPositionLookup by lazy {
		val lookup = HashMap<String, Int>()
		allLevels.forEachIndexed { index, level ->
			lookup.put(level.tag, index)
		}
		return@lazy lookup
	}

	fun getLevelIndex(tag: String) = levelPositionLookup[tag]!!

	fun hasNextLevel(tag: String) = getLevelIndex(tag) < allLevels.size - 1

	fun getNextLevelIndex(tag: String) = if (hasNextLevel(tag)) {
		getLevelIndex(tag) + 1
	} else {
		-1
	}

}
