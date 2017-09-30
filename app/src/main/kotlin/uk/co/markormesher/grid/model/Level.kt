package uk.co.markormesher.grid.model

import android.os.Parcel
import android.os.Parcelable

data class Level(
		val stage: Int,
		val subStage: Int,
		val size: Int,
		val flips: Int,
		val cellStates: Int
): Parcelable {

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(stage)
		parcel.writeInt(subStage)
		parcel.writeInt(size)
		parcel.writeInt(flips)
		parcel.writeInt(cellStates)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR: Parcelable.Creator<Level> {
		override fun createFromParcel(parcel: Parcel) = Level(
				parcel.readInt(),
				parcel.readInt(),
				parcel.readInt(),
				parcel.readInt(),
				parcel.readInt()
		)

		override fun newArray(size: Int): Array<Level?> {
			return arrayOfNulls(size)
		}
	}
}
