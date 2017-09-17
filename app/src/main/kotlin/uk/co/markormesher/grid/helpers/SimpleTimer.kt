package uk.co.markormesher.grid.helpers

import android.os.Parcel
import android.os.Parcelable

class SimpleTimer: Parcelable {

	var paused = true
		private set

	private var previousSpanSums = 0L
	private var currentSpanStart = 0L

	fun start() {
		if (!paused) {
			return
		}

		paused = false
		currentSpanStart = System.currentTimeMillis()
	}

	fun pause() {
		if (paused) {
			return
		}

		paused = true
		previousSpanSums += System.currentTimeMillis() - currentSpanStart
		currentSpanStart = 0L
	}

	fun currentValue(): Long {
		if (currentSpanStart == 0L) {
			return previousSpanSums
		}
		return previousSpanSums + (System.currentTimeMillis() - currentSpanStart)
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(if (paused) 1 else 0)
		parcel.writeLong(previousSpanSums)
		parcel.writeLong(currentSpanStart)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR: Parcelable.Creator<SimpleTimer> {
		override fun createFromParcel(parcel: Parcel): SimpleTimer {
			val timer = SimpleTimer()
			timer.paused = parcel.readInt() == 1
			timer.previousSpanSums = parcel.readLong()
			timer.currentSpanStart = parcel.readLong()
			return timer
		}

		override fun newArray(size: Int): Array<SimpleTimer?> {
			return arrayOfNulls(size)
		}
	}

}
