package uk.co.markormesher.grid

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.merge_game_stats.*
import kotlinx.android.synthetic.main.merge_pause_overlay.*
import kotlinx.android.synthetic.main.merge_win_overlay.*
import uk.co.markormesher.grid.helpers.SimpleTimer
import uk.co.markormesher.grid.model.GameState
import uk.co.markormesher.grid.model.makeSampleGameState
import java.util.*



class GameActivity: AppCompatActivity() {

	companion object {
		const val INITIAL_FLIP_DELAY = 1000L
		const val INITIAL_FLIP_TIMING = 150L
		const val TIMER_UPDATE_FREQUENCY = 200L
	}

	private val random by lazy { Random() }

	// TODO: pass these as options
	private val gameStage = 1
	private val gameLevel = 1
	private val size = 5
	private val totalCellStates = 2

	private var gameState = makeSampleGameState(size, totalCellStates)
	private var lastGameStatus: GameState.Status? = null
	private var initialFlipsStarted = false
	private var initialFlipsRemaining = size + 1
	private val initialFlipsUsed = HashSet<Pair<Int, Int>>()
	private var timer = SimpleTimer()

	private val timerHandler = Handler(Looper.getMainLooper())
	private val timerRunnable = Runnable { updateTimer() }

	private val gameStatusChangeListener = object: GameState.OnStatusChangeListener {
		override fun onStatusChange() {
			onGameStatusChange()
			updateStats()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Fabric.with(this, Crashlytics())
		setState(savedInstanceState)
		initView()
	}

	override fun onResume() {
		super.onResume()
		gameState.addOnStatusChangeListener(gameStatusChangeListener)
		queueTimerUpdates()
	}

	override fun onPause() {
		super.onPause()
		pauseGame()
		cancelTimerUpdates()
		gameState.removeOnStatusChangeListener(gameStatusChangeListener)
	}

    override fun onBackPressed() {
		when (gameState.status) {
			GameState.Status.NOT_STARTED -> return
			GameState.Status.WON -> finish()
			GameState.Status.PAUSED -> startOrResumeGame()
			GameState.Status.IN_PLAY -> pauseGame()
		}
    }

	private fun setState(savedInstanceState: Bundle?) {
		gameState = savedInstanceState?.getParcelable("gameState") ?: gameState
		initialFlipsStarted = savedInstanceState?.getBoolean("initialFlipsStarted") ?: initialFlipsStarted
		initialFlipsRemaining = savedInstanceState?.getInt("initialFlipsRemaining") ?: initialFlipsRemaining
		timer = savedInstanceState?.getParcelable("timer") ?: timer
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putParcelable("gameState", gameState)
		outState.putBoolean("initialFlipsStarted", initialFlipsStarted)
		outState.putInt("initialFlipsRemaining", initialFlipsRemaining)
		outState.putParcelable("timer", timer)
	}

	private fun initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
		setContentView(R.layout.activity_game)

		game_board.gameState = gameState
		updateStats()

		btn_pause_resume.setOnClickListener { startOrResumeGame() }
		btn_pause_quit.setOnClickListener { finish() }

		btn_win_next.setOnClickListener { Toast.makeText(this@GameActivity, "Not implemented yet", Toast.LENGTH_SHORT).show() }
		btn_win_quit.setOnClickListener { finish() }

		if (!initialFlipsStarted) {
			initialFlipsStarted = true
			Handler(Looper.getMainLooper()).postDelayed({ doInitialFlip() }, INITIAL_FLIP_DELAY)
		}

		onGameStatusChange()
	}

	private fun onGameStatusChange() {
		if (lastGameStatus == gameState.status) {
			return
		}
		lastGameStatus = gameState.status

		if (gameState.status == GameState.Status.WON) {
			onGameWon()
		}

		pause_overlay.visibility = if (gameState.status == GameState.Status.PAUSED) View.VISIBLE else View.GONE
		win_overlay.visibility = if (gameState.status == GameState.Status.WON) View.VISIBLE else View.GONE

		stats_wrapper.alpha = if (gameState.status == GameState.Status.IN_PLAY) 1.0f else 0.4f
		game_board.alpha = if (gameState.status == GameState.Status.IN_PLAY) 1.0f else 0.4f
	}

	private fun doInitialFlip() {
		if (initialFlipsRemaining > 0) {
			// generate a unique flip (so we don't undo a previous flip)
			var flip: Pair<Int, Int>
			do {
				flip = Pair(random.nextInt(size), random.nextInt(size))
			} while (initialFlipsUsed.contains(flip))
			initialFlipsUsed.add(flip)

			gameState.flip(flip.first, flip.second, systemAction = true)
			--initialFlipsRemaining
			Handler(Looper.getMainLooper()).postDelayed({ doInitialFlip() }, INITIAL_FLIP_TIMING)
		} else {
			onInitialFlipsComplete()
		}
	}

	private fun onInitialFlipsComplete() {
		startOrResumeGame()
	}

	private fun updateStats() {
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

	private fun startOrResumeGame() {
		if (gameState.status != GameState.Status.PAUSED && gameState.status != GameState.Status.NOT_STARTED) {
			return
		}

		timer.start()
		gameState.status = GameState.Status.IN_PLAY
	}

	private fun pauseGame() {
		if (gameState.status != GameState.Status.IN_PLAY) {
			return
		}

		timer.pause()
		gameState.status = GameState.Status.PAUSED
	}

	private fun onGameWon() {
		timer.pause()
	}
}
