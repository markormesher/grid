package uk.co.markormesher.grid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_pre_game.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import uk.co.markormesher.grid.data.LevelHelper
import uk.co.markormesher.grid.model.GameStateMaker
import uk.co.markormesher.grid.model.NeighbourSets
import java.util.*

class PreGameActivity: AppCompatActivity() {

	private val gameState = GameStateMaker(5, 3, NeighbourSets.ADJACENT).makeGameState()
	private val handler = Handler(Looper.getMainLooper())
	private val flipRunnable = Runnable { doFlip() }
	private val random = Random()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Fabric.with(this, Crashlytics())
		initView()
	}

	override fun attachBaseContext(newBase: Context) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
	}

	private fun initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
		setContentView(R.layout.activity_pre_game)
		btn_start_game_label.text = getString(R.string.pre_game_start_level, LevelHelper.getNextLevelTag())
		btn_start_game.setOnClickListener {
			val intent = Intent(this@PreGameActivity, GameActivity::class.java)
			intent.putExtra("level", LevelHelper.getNextLevelTag())
			startActivity(intent)
		}
		btn_all_levels.setOnClickListener {
			startActivity(Intent(this@PreGameActivity, LevelSelectActivity::class.java))
		}
		btn_badges.setOnClickListener {
			Toast.makeText(this@PreGameActivity, "Not implemented yet", Toast.LENGTH_SHORT).show()
		}
		btn_help.setOnClickListener {
			Toast.makeText(this@PreGameActivity, "Not implemented yet", Toast.LENGTH_SHORT).show()
		}
		game_board.gameState = gameState
	}

	override fun onResume() {
		super.onResume()
		doFlip()
	}

	override fun onPause() {
		super.onPause()
		handler.removeCallbacks(flipRunnable)
	}

	private fun doFlip() {
		gameState.flip(random.nextInt(gameState.size), random.nextInt(gameState.size), systemAction = true)
		handler.postDelayed(flipRunnable, 500L + random.nextInt(1500))
	}

}
