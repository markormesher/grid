package uk.co.markormesher.grid.data

import uk.co.markormesher.grid.helpers.getInt
import uk.co.markormesher.grid.helpers.getStringSet
import uk.co.markormesher.grid.helpers.putInt
import uk.co.markormesher.grid.helpers.putStringSet
import uk.co.markormesher.grid.model.GameStateMaker
import uk.co.markormesher.grid.model.Level
import uk.co.markormesher.grid.model.NeighbourSets

object LevelHelper {

	const val DEFAULT_LEVEL = "1-1"

	val allLevels by lazy {
		val levels = ArrayList<Level>()

		val countPerStage = HashMap<Int, Int>()
		val nextSubStage = { stage: Int ->
			val latestSubStage = countPerStage[stage] ?: 0
			countPerStage[stage] = latestSubStage + 1
			latestSubStage + 1
		}

		// tutorial levels
		levels.add(Level(
				stage = 1, subStage = nextSubStage(1), flips = 2,
				initialState = GameStateMaker(size = 4, qtyCellStates = 2),
				helpTitle = "Tutorial 1/4",
				helpBody = "Some cells have been flipped; tap them to flip them back."
		))
		levels.add(Level(
				stage = 1, subStage = nextSubStage(1), flips = 2,
				initialState = GameStateMaker(size = 4, qtyCellStates = 3),
				helpTitle = "Tutorial 2/4",
				helpBody = "These cells have three states; tap them twice to flip them back."
		))
		levels.add(Level(
				stage = 1, subStage = nextSubStage(1), flips = 2,
				initialState = GameStateMaker(size = 5, qtyCellStates = 2, defaultNeighbours = NeighbourSets.VERTICAL),
				helpTitle = "Tutorial 3/4",
				helpBody = "White dots indicate neighbours. When you flip a cell, its neighbours flip too."
		))
		levels.add(Level(
				stage = 1, subStage = nextSubStage(1), flips = 2,
				initialState = GameStateMaker(size = 5, qtyCellStates = 2, defaultNeighbours = NeighbourSets.HORIZONTAL),
				helpTitle = "Tutorial 4/4",
				helpBody = "Neighbours can be in different places; always check the dots."
		))

		// stage 2: get used to neighbours
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 2,
				initialState = GameStateMaker(size = 5, qtyCellStates = 2, defaultNeighbours = NeighbourSets.VERTICAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 2,
				initialState = GameStateMaker(size = 5, qtyCellStates = 3, defaultNeighbours = NeighbourSets.VERTICAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 3,
				initialState = GameStateMaker(size = 6, qtyCellStates = 2, defaultNeighbours = NeighbourSets.VERTICAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 3,
				initialState = GameStateMaker(size = 6, qtyCellStates = 3, defaultNeighbours = NeighbourSets.VERTICAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 2,
				initialState = GameStateMaker(size = 5, qtyCellStates = 2, defaultNeighbours = NeighbourSets.HORIZONTAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 2,
				initialState = GameStateMaker(size = 5, qtyCellStates = 3, defaultNeighbours = NeighbourSets.HORIZONTAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 3,
				initialState = GameStateMaker(size = 6, qtyCellStates = 2, defaultNeighbours = NeighbourSets.HORIZONTAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 3,
				initialState = GameStateMaker(size = 6, qtyCellStates = 3, defaultNeighbours = NeighbourSets.HORIZONTAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 1,
				initialState = GameStateMaker(size = 5, qtyCellStates = 2, defaultNeighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 2,
				initialState = GameStateMaker(size = 5, qtyCellStates = 2, defaultNeighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 3,
				initialState = GameStateMaker(size = 6, qtyCellStates = 2, defaultNeighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 1,
				initialState = GameStateMaker(size = 5, qtyCellStates = 2, defaultNeighbours = NeighbourSets.DIAGONAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 2,
				initialState = GameStateMaker(size = 5, qtyCellStates = 2, defaultNeighbours = NeighbourSets.DIAGONAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 3,
				initialState = GameStateMaker(size = 6, qtyCellStates = 2, defaultNeighbours = NeighbourSets.DIAGONAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 4,
				initialState = GameStateMaker(size = 6, qtyCellStates = 2, defaultNeighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 5,
				initialState = GameStateMaker(size = 6, qtyCellStates = 2, defaultNeighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 5,
				initialState = GameStateMaker(size = 6, qtyCellStates = 2, defaultNeighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 4,
				initialState = GameStateMaker(size = 6, qtyCellStates = 2, defaultNeighbours = NeighbourSets.DIAGONAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 5,
				initialState = GameStateMaker(size = 6, qtyCellStates = 2, defaultNeighbours = NeighbourSets.DIAGONAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 5,
				initialState = GameStateMaker(size = 6, qtyCellStates = 2, defaultNeighbours = NeighbourSets.DIAGONAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 3,
				initialState = GameStateMaker(size = 6, qtyCellStates = 3, defaultNeighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 4,
				initialState = GameStateMaker(size = 6, qtyCellStates = 3, defaultNeighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 5,
				initialState = GameStateMaker(size = 6, qtyCellStates = 3, defaultNeighbours = NeighbourSets.ADJACENT)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 3,
				initialState = GameStateMaker(size = 6, qtyCellStates = 3, defaultNeighbours = NeighbourSets.DIAGONAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 4,
				initialState = GameStateMaker(size = 6, qtyCellStates = 3, defaultNeighbours = NeighbourSets.DIAGONAL)
		))
		levels.add(Level(
				stage = 2, subStage = nextSubStage(2), flips = 5,
				initialState = GameStateMaker(size = 6, qtyCellStates = 3, defaultNeighbours = NeighbourSets.DIAGONAL)
		))

		return@lazy levels
	}

	private val levelsCompleted by lazy {
		getStringSet("levelsCompleted")
	}

	private val levelPositionLookup by lazy {
		val lookup = HashMap<String, Int>()
		allLevels.forEachIndexed { index, level ->
			lookup.put(level.tag, index)
		}
		return@lazy lookup
	}

	private fun getLevelIndex(tag: String) = levelPositionLookup[tag]!!

	fun getLevel(tag: String): Level {
		return allLevels[getLevelIndex(tag)]
	}

	fun hasNextLevel(level: Level) = getLevelIndex(level.tag) < allLevels.size - 1

	fun getNextLevelTag() = allLevels.firstOrNull { !isLevelCompleted(it) }?.tag ?: DEFAULT_LEVEL

	fun getNextLevelTag(level: Level) = if (hasNextLevel(level)) {
		allLevels[getLevelIndex(level.tag) + 1].tag
	} else {
		DEFAULT_LEVEL
	}

	fun isLevelCompleted(level: Level) = levelsCompleted.contains(level.tag)

	fun markLevelCompleted(level: Level, score: Int = 0) {
		levelsCompleted.add(level.tag)
		putStringSet("levelsCompleted", levelsCompleted)
		putInt("levelScore-${level.tag}", score)
	}

	fun getLevelScore(level: Level): Int {
		if (!isLevelCompleted(level)) {
			return 0
		}
		return getInt("levelScore-${level.tag}", 0)
	}

}
