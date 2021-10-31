package com.example.cinema

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.NumberPicker.OnValueChangeListener
import androidx.fragment.app.DialogFragment


class NumberPickerDialog: DialogFragment() {
    private var valueChangeListener: OnValueChangeListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val numberPicker = NumberPicker(activity)
        numberPicker.minValue = 1
        numberPicker.maxValue = 10
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("인원 선택")
        builder.setMessage("인원 : ")
        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which ->
                valueChangeListener!!.onValueChange(
                    numberPicker,
                    numberPicker.value, numberPicker.value
                )
            })
        builder.setNegativeButton("CANCEL",
            DialogInterface.OnClickListener { dialog, which ->

            })
        builder.setView(numberPicker)
        return builder.create()
    }

    fun getValueChangeListener(): OnValueChangeListener? {
        return valueChangeListener
    }

    fun setValueChangeListener(valueChangeListener: OnValueChangeListener?) {
        this.valueChangeListener = valueChangeListener
    }
}