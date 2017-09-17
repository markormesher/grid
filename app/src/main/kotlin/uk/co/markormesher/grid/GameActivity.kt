package uk.co.markormesher.grid

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.merge_game_stats.*
import uk.co.markormesher.grid.helpers.SimpleTimer
import uk.co.markormesher.grid.model.GameState
import uk.co.markormesher.grid.model.makeSampleGameState
import java.util.*

class GameActivity: AppCompatActivity() {

	companion object {
		const val INITIAL_FLIP_DELAY = 1000L
		const val INITIAL_FLIP_TIMING = 250L
		const val TIMER_UPDATE_FREQUENCY = 200L
	}

	private val random by lazy { Random() }

	// TODO: pass these as options
	private val gameStage = 1
	private val gameLevel = 1
	private val size = 5
	private val totalCellStates = 2

	private var gameState: GameState = makeSampleGameState(size, totalCellStates)
	private var gameStarted = false
	private var initialFlipsStarted = false
	private var initialFlipsRemaining = size
	private var timer = SimpleTimer()
	private var prevPauseState = gameState.paused

	private val timerHandler = Handler(Looper.getMainLooper())
	private val timerRunnable = Runnable { updateTimer() }

	private val gameStateChangeListener = object: GameState.OnStateChangeListener {
		override fun onStateChange() {
			if (prevPauseState != gameState.paused) {
				if (gameState.paused) {
					onGamePaused()
				} else {
					onGameResumed()
				}
				prevPauseState = gameState.paused
			}

			updateStats()

			if (gameStarted && !gameState.paused && gameState.isWinningState()) {
				onGameWon()
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setState(savedInstanceState)
		initView()
	}

	override fun onResume() {
		super.onResume()

		gameState.addOnStateChangeListener(gameStateChangeListener)
		queueTimerUpdates()

		if (gameStarted) {
			gameState.paused = false
		}
	}

	override fun onPause() {
		super.onPause()

		if (gameStarted) {
			gameState.paused = true
		}

		cancelTimerUpdates()
		gameState.removeOnStateChangeListener(gameStateChangeListener)
	}

	private fun setState(savedInstanceState: Bundle?) {
		gameState = savedInstanceState?.getParcelable("gameState") ?: gameState
		gameStarted = savedInstanceState?.getBoolean("gameStarted") ?: gameStarted
		initialFlipsStarted = savedInstanceState?.getBoolean("initialFlipsStarted") ?: initialFlipsStarted
		initialFlipsRemaining = savedInstanceState?.getInt("initialFlipsRemaining") ?: initialFlipsRemaining
		prevPauseState = savedInstanceState?.getBoolean("prevPauseState") ?: prevPauseState
		timer = savedInstanceState?.getParcelable("timer") ?: timer
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putParcelable("gameState", gameState)
		outState.putBoolean("gameStarted", gameStarted)
		outState.putBoolean("initialFlipsStarted", initialFlipsStarted)
		outState.putInt("initialFlipsRemaining", initialFlipsRemaining)
		outState.putBoolean("prevPauseState", prevPauseState)
		outState.putParcelable("timer", timer)
	}

	private fun initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
		setContentView(R.layout.activity_game)

		game_board.gameState = gameState
		updateStats()

		if (!initialFlipsStarted) {
			gameState.paused = true
			initialFlipsStarted = true
			Handler(Looper.getMainLooper()).postDelayed({ doInitialFlip() }, INITIAL_FLIP_DELAY)
		}
	}

	private fun doInitialFlip() {
		if (initialFlipsRemaining > 0) {
			gameState.flip(random.nextInt(size), random.nextInt(size), systemAction = true)
			--initialFlipsRemaining
			Handler(Looper.getMainLooper()).postDelayed({ doInitialFlip() }, INITIAL_FLIP_TIMING)
		} else {
			onInitialFlipsComplete()
		}
	}

	private fun onInitialFlipsComplete() {
		gameState.paused = false
		gameStarted = true
	}

	private fun updateStats() {
		if (gameState.paused || !gameStarted) {
			stats_wrapper.alpha = 0.4F
		} else {
			stats_wrapper.alpha = 1.0F
		}

		stat_level.text = getString(R.string.stat_level_value_format, gameStage, gameLevel)
		stat_progress.text = getString(R.string.stat_progress_value_format, gameState.qtyWinningCells(), gameState.size * gameState.size)
		stat_flips.text = getString(R.string.stat_flips_value_format, gameState.userFlipCount)
	}

	private fun queueTimerUpdates() {
		timerHandler.postDelayed(timerRunnable, TIMER_UPDATE_FREQUENCY)
	}

	private fun cancelTimerUpdates() {
		timerHandler.removeCallbacks(timerRunnable)
	}

	private fun updateTimer() {
		val currentTimer = timer.currentValue()
		val timerSecs = (currentTimer / 1000).rem(60)
		val timerMins = (currentTimer / 1000) / 60
		stat_time.text = getString(R.string.stat_time_value_format, timerMins, timerSecs)
		queueTimerUpdates()
	}

	private fun onGamePaused() {
		timer.pause()
	}

	private fun onGameResumed() {
		timer.start()
	}

	private fun onGameWon() {
		Toast.makeText(this@GameActivity, "You Won!", Toast.LENGTH_SHORT).show()
		gameState.paused = true
	}
}
