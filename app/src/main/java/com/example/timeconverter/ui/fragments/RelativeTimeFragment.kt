package com.example.timeconverter.ui.fragments

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.timeconverter.databinding.FragmentRelativeTimeBinding
import com.example.timeconverter.ui.viewmodels.RelativeTimeViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.math.abs

class RelativeTimeFragment : Fragment() {
    private lateinit var binding: FragmentRelativeTimeBinding
    private lateinit var relativeTimeViewModel: RelativeTimeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRelativeTimeBinding.inflate(inflater)
        relativeTimeViewModel = ViewModelProvider(this)[RelativeTimeViewModel::class.java]
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            dateInputTv.setOnClickListener { showDatePickerDialog() }
            timeInputTv.setOnClickListener { showTimePickerDialog() }
            convertBtn.setOnClickListener { convertToRelativeTime() }
        }
    }

    private fun convertToRelativeTime() {
        relativeTimeViewModel.apply {
            if (pickedDate == "") {
                Toast.makeText(requireContext(), "Please pick a date", Toast.LENGTH_SHORT).show()
                return
            }

            if (pickedHours == -1) {
                Toast.makeText(requireContext(), "Please pick a time", Toast.LENGTH_SHORT).show()
                return
            }

            val pickedDateSplit = pickedDate.split("/").map { it.toInt() }

            val todayInMillis = Calendar.getInstance().timeInMillis
            val pickedDateInMillis = Calendar.getInstance().also {
                it.set(pickedDateSplit[2], pickedDateSplit[1]-1, pickedDateSplit[0], pickedHours, pickedMinutes)
            }.timeInMillis

            val todayInSecs = todayInMillis/1000
            val pickedDateInSeconds = pickedDateInMillis/1000

            val secondsDiff = todayInSecs - pickedDateInSeconds
            if (secondsDiff in 1..59) {
                setResult("$secondsDiff seconds ago")
                return
            }
            if (secondsDiff in -1L downTo -59L) {
                setResult("${abs(secondsDiff)} seconds in future")
                return
            }

            val minutesDiff = secondsDiff/60

            if (minutesDiff in 1..59) {
                setResult("$minutesDiff minutes ago")
                return
            }
            if (minutesDiff in -1L downTo -59L) {
                setResult("${abs(minutesDiff)} minutes in future")
                return
            }

            val hoursDiff = secondsDiff/3600
            if (hoursDiff in 1..23) {
                setResult("$hoursDiff hours ago")
                return
            }
            if (hoursDiff in -1L downTo -23L) {
                setResult("${abs(hoursDiff)} hours in future")
                return
            }

            val daysDiff = secondsDiff/(3600*24)
            if (daysDiff in 7..29) {
                setResult("${daysDiff / 7} weeks ago")
                return
            }
            if (daysDiff in -7L downTo -29L) {
                setResult("${abs(daysDiff / 7)} weeks in future")
                return
            }
            if (daysDiff in 1..6) {
                setResult("$daysDiff days ago")
                return
            }
            if (daysDiff in -1L downTo -6L) {
                setResult("${abs(daysDiff)} days in future")
                return
            }

            val monthsDiff = secondsDiff/(3600*24*30)
            if (monthsDiff in 1..11) {
                setResult("$monthsDiff months ago")
                return
            }
            if (monthsDiff in -1L downTo -11L) {
                setResult("${abs(monthsDiff)} months in future")
                return
            }

            val yearsDiff = secondsDiff/(3600*24*30*12)
            if (yearsDiff > 0) {
                setResult("$yearsDiff years ago")
                return
            }
            if (yearsDiff < 0) {
                setResult("${abs(yearsDiff)} years in future")
                return
            }
        }
    }

    private fun setResult(relativeTime: String) {
        binding.relativeTimeTv.text = relativeTime
    }

    private fun showDatePickerDialog() {
        val materialDatePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").build()

        materialDatePicker.addOnPositiveButtonClickListener {
            it?.let {
                val date = DateFormat.format("dd/MM/yyyy", Date(it)).toString()
                binding.dateInputTv.text = date

                relativeTimeViewModel.pickedDate = date
            }
        }

        materialDatePicker.show(childFragmentManager, "TAG")
    }

    private fun showTimePickerDialog() {
        val materialTimePicker =
            MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()

        materialTimePicker.addOnPositiveButtonClickListener {
            val newHour = materialTimePicker.hour
            val newMinute = materialTimePicker.minute

            relativeTimeViewModel.pickedHours = newHour
            relativeTimeViewModel.pickedMinutes = newMinute

            binding.timeInputTv.text = "$newHour:$newMinute hours"
        }

        materialTimePicker.show(childFragmentManager, "TAG")
    }
}