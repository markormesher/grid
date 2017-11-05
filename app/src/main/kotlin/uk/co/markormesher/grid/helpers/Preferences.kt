package uk.co.markormesher.grid.helpers

import android.content.SharedPreferences
import android.preference.PreferenceManager
import uk.co.markormesher.grid.BaseApplication

private fun getPrefs(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaseApplication.context)!!

fun getInt(key: String, default: Int): Int {
	return getPrefs().getInt(key, default)
}

fun putInt(key: String, value: Int) {
	val editor = getPrefs().edit()
	editor.putInt(key, value)
	editor.apply()
}

fun getString(key: String, default: String): String {
	return getPrefs().getString(key, default)
}

fun putString(key: String, value: String) {
	val editor = getPrefs().edit()
	editor.putString(key, value)
	editor.apply()
}

fun getStringSet(key: String): MutableSet<String> {
	return getPrefs().getStringSet(key, HashSet<String>())
}

fun putStringSet(key: String, value: Set<String>) {
	val editor = getPrefs().edit()
	editor.putStringSet(key, value)
	editor.apply()
}
