package uk.co.markormesher.grid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_level_select.*
import kotlinx.android.synthetic.main.list_item_stage.view.*
import kotlinx.android.synthetic.main.list_item_sub_stage.view.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import uk.co.markormesher.grid.data.LevelHelper

class LevelSelectActivity: AppCompatActivity() {

	private val columns = 6

	private val levelListItems by lazy {
		val items = ArrayList<LevelListItem>()
		var lastStage = 0
		LevelHelper.allLevels.forEach { level ->
			if (level.stage != lastStage) {
				lastStage = level.stage
				items.add(LevelListItem(true, level.stage))
			}
			items.add(LevelListItem(false, level.subStage, level.tag))
		}
		return@lazy items
	}

	private val nextLevelTag = LevelHelper.getNextLevelTag()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Fabric.with(this, Crashlytics())
		initView()
	}

	override fun attachBaseContext(newBase: Context) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
	}

	private fun initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
		setContentView(R.layout.activity_level_select)

		val layoutManager = GridLayoutManager(this, columns)
		layoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
			override fun getSpanSize(position: Int): Int {
				return if (listAdapter.getItemViewType(position) == listAdapter.STAGE) {
					columns
				} else {
					1
				}
			}
		}

		recycler_view.adapter = listAdapter
		recycler_view.layoutManager = layoutManager
	}

	private val listAdapter = object: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

		val STAGE = 1
		val SUB_STAGE = 2

		private val inflater by lazy { LayoutInflater.from(applicationContext) }

		override fun getItemCount() = levelListItems.size

		override fun getItemViewType(position: Int) = if (levelListItems[position].isStage) STAGE else SUB_STAGE

		override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
			return if (viewType == STAGE) {
				StageViewHolder(inflater.inflate(R.layout.list_item_stage, parent, false))
			} else {
				SubStageViewHolder(inflater.inflate(R.layout.list_item_sub_stage, parent, false))
			}
		}

		override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
			val type = getItemViewType(position)
			if (type == STAGE) {
				with(holder as StageViewHolder) {
					label.text = getString(R.string.level_select_stage, levelListItems[position].number)
				}
			} else {
				with(holder as SubStageViewHolder) {
					val levelTag = levelListItems[position].tag
					val level = LevelHelper.getLevel(levelTag)
					if (LevelHelper.isLevelCompleted(level.tag) || level.tag == nextLevelTag) {
						label.text = getString(R.string.level_select_sub_stage, levelListItems[position].number)
						lockIcon.visibility = View.INVISIBLE
						view.setOnClickListener {
							val intent = Intent(this@LevelSelectActivity, GameActivity::class.java)
							intent.putExtra("level", level.tag)
							startActivity(intent)
							finish()
						}
					} else {
						label.text = null
						lockIcon.visibility = View.VISIBLE
						view.setOnClickListener(null)
					}
				}
			}
		}

	}

	class StageViewHolder(val view: View): RecyclerView.ViewHolder(view) {
		val label = view.stage_label!!
	}

	class SubStageViewHolder(val view: View): RecyclerView.ViewHolder(view) {
		val label = view.sub_stage_label!!
		val lockIcon = view.lock_icon!!
	}

	data class LevelListItem(val isStage: Boolean, val number: Int, val tag: String = "")

}
