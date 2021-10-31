package com.example.cinema

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*


class DatePickerFragment : DialogFragment(), OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)

        val mDatePicker = DatePickerDialog(activity as Context, this, year, month, day)
        mDatePicker.datePicker.minDate = System.currentTimeMillis()
        return mDatePicker
    }

    override fun onDateSet(
        datePicker: DatePicker,
        year: Int,
        month: Int,
        day: Int
    ) {
        val activity = getActivity() as MainActivity
        activity.processDatePickerResult(year, month+1, day)
    }
}