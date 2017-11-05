package uk.co.markormesher.grid.ui

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import kotlinx.android.synthetic.main.ui_custom_dialog.*
import uk.co.markormesher.grid.R

class CustomDialog(context: Context): Dialog(context) {

	private var onCloseListener: (() -> Unit)? = null

	init {
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		setContentView(R.layout.ui_custom_dialog)
		setCancelable(false)
		setCanceledOnTouchOutside(false)
		dialog_ok_btn.setOnClickListener {
			onCloseListener?.invoke()
			dismiss()
		}
	}

	fun withTitle(title: String?): CustomDialog {
		if (title == null) {
			dialog_title.visibility = View.GONE
		} else {
			dialog_title.visibility = View.VISIBLE
			dialog_title.text = title
		}
		return this
	}

	fun withBody(body: String?): CustomDialog {
		if (body == null) {
			dialog_body.visibility = View.GONE
		} else {
			dialog_body.visibility = View.VISIBLE
			dialog_body.text = body
		}
		return this
	}

	fun withOnCloseListener(onCloseListener: () -> Unit): CustomDialog {
		this.onCloseListener = onCloseListener
		return this
	}

}
