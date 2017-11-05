package uk.co.markormesher.grid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.merge_game_info.*
import kotlinx.android.synthetic.main.merge_pause_overlay.*
import kotlinx.android.synthetic.main.merge_win_overlay.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import uk.co.markormesher.grid.data.LevelHelper
import uk.co.markormesher.grid.data.LevelHelper.markLevelCompleted
import uk.co.markormesher.grid.helpers.SimpleTimer
import uk.co.markormesher.grid.helpers.randomString
import uk.co.markormesher.grid.model.GameState
import uk.co.markormesher.grid.model.Level
import uk.co.markormesher.grid.ui.CustomDialog
import java.util.*

class GameActivity: AppCompatActivity() {

	companion object {
		const val INITIAL_FLIP_DELAY = 1000L
		const val INITIAL_FLIP_TIMING = 150L
		const val TIMER_UPDATE_FREQUENCY = 200L
	}

	private val random = Random()

	private var initialised = false
	private lateinit var level: Level
	private lateinit var gameState: GameState
	private var lastGameStatus: GameState.Status? = null

	private var helpDialogShown = false
	private var initialFlipsStarted = false
	private var initialFlipsDone = 0
	private val initialFlipsUsed = HashSet<Pair<Int, Int>>()
	private var timer = SimpleTimer()

	private val timerHandler = Handler(Looper.getMainLooper())
	private val timerRunnable = Runnable { updateTimer() }

	private val gameCellChangeListener = object: GameState.OnCellChangeListener {
		override fun onCellChange(row: Int, col: Int) {
			updateStats()
		}
	}

	private val gameStatusChangeListener = object: GameState.OnStatusChangeListener {
		override fun onStatusChange() {
			onGameStatusChange()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Fabric.with(this, Crashlytics())

		if (!initialised) {
			initialised = true
			level = LevelHelper.getLevel(intent?.extras?.getString("level") ?: LevelHelper.DEFAULT_LEVEL)
			gameState = level.initialState.makeGameState()
		}

		setState(savedInstanceState)
		showHelpDialog()
		initView()
	}

	override fun attachBaseContext(newBase: Context) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
	}

	override fun onResume() {
		super.onResume()
		gameState.addOnCellChangeListener(gameCellChangeListener)
		gameState.addOnStatusChangeListener(gameStatusChangeListener)
		queueTimerUpdates()
	}

	override fun onPause() {
		super.onPause()
		pauseGame()
		cancelTimerUpdates()
		gameState.removeOnCellChangeListener(gameCellChangeListener)
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

	private fun showHelpDialog() {
		if (!helpDialogShown && (level.helpTitle != null || level.helpBody != null)) {
			helpDialogShown = true
			CustomDialog(this)
					.withTitle(level.helpTitle)
					.withBody(level.helpBody)
					.withOnCloseListener({ startInitialFlips() })
					.show()
		}
	}

	private fun setState(savedInstanceState: Bundle?) {
		gameState = savedInstanceState?.getParcelable("gameState") ?: gameState
		initialFlipsStarted = savedInstanceState?.getBoolean("initialFlipsStarted") ?: initialFlipsStarted
		initialFlipsDone = savedInstanceState?.getInt("initialFlipsDone") ?: initialFlipsDone
		timer = savedInstanceState?.getParcelable("timer") ?: timer
		level = savedInstanceState?.getParcelable("level") ?: level
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putParcelable("gameState", gameState)
		outState.putBoolean("initialFlipsStarted", initialFlipsStarted)
		outState.putInt("initialFlipsDone", initialFlipsDone)
		outState.putParcelable("timer", timer)
		outState.putParcelable("level", level)
	}

	private fun initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
		setContentView(R.layout.activity_game)

		game_board.gameState = gameState
		updateStats()

		state_transition_display.gameState = gameState
		state_transition_display.decorator = game_board.decorator

		btn_pause_resume.setOnClickListener { startOrResumeGame() }
		btn_pause_quit.setOnClickListener { finish() }

		if (LevelHelper.hasNextLevel(level)) {
			btn_win_next.setOnClickListener {
				val intent = Intent(this@GameActivity, GameActivity::class.java)
				intent.putExtra("level", LevelHelper.getNextLevelTag(level))
				startActivity(intent)
				finish()
			}
		} else {
			btn_win_next.alpha = 0.4f
		}
		btn_win_quit.setOnClickListener { finish() }

		if (!helpDialogShown) {
			startInitialFlips()
		}

		onGameStatusChange()
	}

	private fun onGameStatusChange() {
		if (lastGameStatus == gameState.status) {
			return
		}
		lastGameStatus = gameState.status

		if (gameState.status == GameState.Status.WON) {
			val score = level.getScore(gameState.userFlipCount)
			markLevelCompleted(level, score)
			onGameWon(score)
		}

		pause_overlay.visibility = if (gameState.status == GameState.Status.PAUSED) View.VISIBLE else View.GONE
		win_overlay.visibility = if (gameState.status == GameState.Status.WON) View.VISIBLE else View.GONE

		stats_wrapper.alpha = if (gameState.status == GameState.Status.IN_PLAY) 1.0f else 0.4f
		game_board.alpha = if (gameState.status == GameState.Status.IN_PLAY) 1.0f else 0.4f
	}

	private fun startInitialFlips() {
		if (!initialFlipsStarted) {
			initialFlipsStarted = true
			Handler(Looper.getMainLooper()).postDelayed({ doInitialFlip() }, INITIAL_FLIP_DELAY)
		}
	}

	private fun doInitialFlip() {
		if (initialFlipsDone < level.flips) {
			// generate a unique flip (so we don't undo a previous flip)
			var flip: Pair<Int, Int>
			do {
				flip = Pair(random.nextInt(gameState.size), random.nextInt(gameState.size))
			} while (initialFlipsUsed.contains(flip))
			initialFlipsUsed.add(flip)

			gameState.flip(flip.first, flip.second, systemAction = true)
			++initialFlipsDone
			Handler(Looper.getMainLooper()).postDelayed({ doInitialFlip() }, INITIAL_FLIP_TIMING)
		} else {
			onInitialFlipsComplete()
		}
	}

	private fun onInitialFlipsComplete() {
		startOrResumeGame()
	}

	private fun updateStats() {
		level_title.text = getString(R.string.stat_level_value_format, level.stage, level.subStage)
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

	private fun onGameWon(score: Int) {
		timer.pause()
		win_title.text = randomString(R.array.win_titles)

		var delay = 0L
		val stars = arrayOf(star_1, star_2, star_3)
		stars.forEachIndexed { index, view ->
			if (index < score) {
				view.setImageResource(R.drawable.ic_star_white_48dp)
				view.alpha = 1.0f
			} else {
				view.setImageResource(R.drawable.ic_star_border_white_48dp)
				view.alpha = 0.4f
			}
		}

		for (i in 0 until score) {
			delay += 300L
			stars[i].animate()
					.rotationBy(360F)
					.setDuration(600L)
					.setStartDelay(delay)
					.start()
		}
	}
}
