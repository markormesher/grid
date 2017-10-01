package uk.co.markormesher.grid.model

import android.os.Parcel
import android.os.Parcelable
import uk.co.markormesher.grid.data.LevelHelper

data class Level(
		val stage: Int,
		val subStage: Int,
		val flips: Int,
		val initialState: GameState,
		val helpTitle: String? = null,
		val helpBody: String? = null
): Parcelable {

	val tag: String
		get() = "$stage-$subStage"

	fun hasNextLevel() = LevelHelper.hasNextLevel(tag)

	fun getNextLevelIndex() = LevelHelper.getNextLevelIndex(tag)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(stage)
		parcel.writeInt(subStage)
		parcel.writeInt(flips)
		parcel.writeParcelable(initialState, 0)
		parcel.writeString(helpTitle)
		parcel.writeString(helpBody)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR: Parcelable.Creator<Level> {
		override fun createFromParcel(parcel: Parcel) = Level(
				parcel.readInt(),
				parcel.readInt(),
				parcel.readInt(),
				parcel.readParcelable(GameState::class.java.classLoader),
				parcel.readString(),
				parcel.readString()
		)

		override fun newArray(size: Int): Array<Level?> {
			return arrayOfNulls(size)
		}
	}
}
