package uk.co.markormesher.grid

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_pre_game.*
import uk.co.markormesher.grid.model.makeSampleGameState
import java.util.*

class PreGameActivity : AppCompatActivity() {

	private val size = 6
	private val gameState = makeSampleGameState(size, 2)
	private val handler = Handler(Looper.getMainLooper())
	private val flipRunnable = Runnable { doFlip() }
    private val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initView()
	}

	private fun initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_pre_game)
		btn_start_game.setOnClickListener {
			startActivity(Intent(this@PreGameActivity, GameActivity::class.java))
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
		gameState.flip(random.nextInt(size), random.nextInt(size), systemAction = true)
		handler.postDelayed(flipRunnable, 1500L)
	}

}
