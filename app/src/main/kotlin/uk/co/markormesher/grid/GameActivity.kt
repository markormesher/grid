package uk.co.markormesher.grid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_game.*
import uk.co.markormesher.grid.model.makeSampleGameState

class GameActivity: AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_game)
		game_board.state = makeSampleGameState(5)
	}

}
