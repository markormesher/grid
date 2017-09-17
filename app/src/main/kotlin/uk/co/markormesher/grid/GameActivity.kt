package uk.co.markormesher.grid

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*
import uk.co.markormesher.grid.model.GameState
import uk.co.markormesher.grid.model.makeSampleGameState
import java.util.*

class GameActivity: AppCompatActivity() {

	companion object {
		const val INITIAL_FLIP_TIMING = 250L
	}

	private val random by lazy { Random() }

	// TODO: pass these as difficulty options
	private val size = 5
	private val totalCellStates = 2
	private var initialFlipsRemaining = size

	private var initialised = false

	private var gameState: GameState = makeSampleGameState(size, totalCellStates)

	private val gameStateChangeListener = object: GameState.OnStateChangeListener {
		override fun onStateChange() {
			if (gameState.isWinningState()) {
				onGameWon()
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setState(savedInstanceState)
		gameState.addOnStateChangeListener(gameStateChangeListener)
		initView()
	}

	private fun setState(savedInstanceState: Bundle?) {
		gameState = savedInstanceState?.getParcelable("gameState") ?: gameState
		initialised = savedInstanceState?.getBoolean("initialised") ?: initialised
		initialFlipsRemaining = savedInstanceState?.getInt("initialFlipsRemaining") ?: initialFlipsRemaining
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putParcelable("gameState", gameState)
		outState.putBoolean("initialised", initialised)
		outState.putInt("initialFlipsRemaining", initialFlipsRemaining)
	}

	private fun initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

		setContentView(R.layout.activity_game)
		game_board.gameState = gameState

		if (!initialised) {
			Handler(Looper.getMainLooper()).postDelayed({ queueInitialFlip() }, INITIAL_FLIP_TIMING)
		}
	}

	private fun queueInitialFlip() {
		if (initialFlipsRemaining > 0) {
			gameState.flip(random.nextInt(size), random.nextInt(size))
			--initialFlipsRemaining
			Handler(Looper.getMainLooper()).postDelayed({ queueInitialFlip() }, INITIAL_FLIP_TIMING)
		} else {
			onInitialisationComplete()
		}
	}

	private fun onInitialisationComplete() {
		game_board.alpha = 1.0f
	}

	private fun onGameWon() {
		Toast.makeText(this@GameActivity, "You Won!", Toast.LENGTH_SHORT).show()
		finish()
	}
}
