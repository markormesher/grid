package uk.co.markormesher.grid

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pre_game.*

class PreGameActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initView()
	}

	private fun initView() {
		setContentView(R.layout.activity_pre_game)
		btn_start_game.setOnClickListener {
			startActivity(Intent(this@PreGameActivity, GameActivity::class.java))
		}
	}

}
