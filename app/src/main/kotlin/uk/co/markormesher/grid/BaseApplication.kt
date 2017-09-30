package uk.co.markormesher.grid

import android.app.Application
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                //.setDefaultFontPath("fonts/.ttf")
                .build()
        )
    }

}