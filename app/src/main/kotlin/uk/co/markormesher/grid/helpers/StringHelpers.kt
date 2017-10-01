package uk.co.markormesher.grid.helpers

import android.content.Context
import android.support.annotation.ArrayRes
import java.util.*

private val random by lazy { Random() }

fun Context.randomString(@ArrayRes arrayResId: Int): String {
	val array = resources.getStringArray(arrayResId)
	return array[random.nextInt(array.size)]
}
