package uk.co.markormesher.grid

import android.app.Application
import android.content.Context
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class BaseApplication: Application() {

	override fun onCreate() {
		super.onCreate()
		context = applicationContext
		CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/fantasquesansmono-regular.otf")
				.build()
		)
	}

	companion object {
		lateinit var context: Context
	}

}
