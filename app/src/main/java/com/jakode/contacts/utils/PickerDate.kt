package com.jakode.contacts.utils

import android.app.Activity
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Typeface
import android.util.Log
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import com.wdullaer.materialdatetimepicker.JalaliCalendar
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

object PickerDate : DialogInterface.OnCancelListener, DatePickerDialog.OnDateSetListener {
    var textView: TextView? = null
    private var dpd: DatePickerDialog? = null
    var calendarType = DatePickerDialog.Type.GREGORIAN
    private var font: Typeface? = null
    var title: String? = null
    var modeDarkDate = false
    var vibrateDate = true
    var dismissDateOnPause = false
    var showYearFirst = false

    fun show(fragmentManager: FragmentManager, tag: String) {
        val now =
            if (calendarType == DatePickerDialog.Type.GREGORIAN) Calendar.getInstance()
            else JalaliCalendar.getInstance()

        /* It is recommended to always create a new instance whenever you need to show a Dialog.
        The sample app is reusing them because it is useful when looking for regressions
        during testing */

        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                calendarType,
                this,
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
            )
        } else {
            dpd!!.calendarType = calendarType
            dpd!!.initialize(
                this,
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
            )
        }

        // set font
        when (calendarType) {
            DatePickerDialog.Type.GREGORIAN -> dpd!!.setFont(null)
            DatePickerDialog.Type.JALALI -> font?.let { dpd!!.setFont(font) }
        }

        // setting
        dpd!!.isThemeDark = modeDarkDate
        dpd!!.vibrate(vibrateDate)
        dpd!!.dismissOnPause(dismissDateOnPause)
        dpd!!.showYearPickerFirst(showYearFirst)
        dpd!!.version = DatePickerDialog.Version.VERSION_1
        title?.let { dpd!!.setTitle(this.title) }
        dpd!!.setOnCancelListener(this)

        dpd!!.show(fragmentManager, tag)
    }

    fun setFont(activity: Activity, id: Int) {
        font = ResourcesCompat.getFont(activity, id)!!
    }

    fun isDarkTheme(activity: Activity): Boolean {
        return activity.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onCancel(dialog: DialogInterface?) {
        Log.i("DatePicker", "Dialog was cancelled")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var myMonthOfYear = monthOfYear
        val date = "$dayOfMonth/${++myMonthOfYear}/$year"
        textView?.let { it.hint = date }
    }
}