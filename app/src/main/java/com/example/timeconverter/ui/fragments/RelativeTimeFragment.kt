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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class RelativeTimeFragment : Fragment() {
    private lateinit var binding: FragmentRelativeTimeBinding
    private lateinit var relativeTimeViewModel: RelativeTimeViewModel
    private lateinit var resultString: String

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
            if (pickedDateMillis == -1L) {
                Toast.makeText(requireContext(), "Please pick a date", Toast.LENGTH_SHORT).show()
                return
            }

            if (pickedHours == -1) {
                Toast.makeText(requireContext(), "Please pick a time", Toast.LENGTH_SHORT).show()
                return
            }

            val totalTimeMillis = timeToMillis(pickedHours, pickedMinutes, 0)
            val sdf = SimpleDateFormat("HH:mm:ss")

            val currentTime = sdf.format(Date()) ?: "00:00:00"
            val timeSplit = currentTime.split(":")

            val currentTimeMillis =
                timeToMillis(timeSplit[0].toInt(), timeSplit[1].toInt(), timeSplit[2].toInt())
            val timeDiffMillis = currentTimeMillis - totalTimeMillis

            val totalSecsDiff = timeDiffMillis / 1000
            val totalMinutesDiff = totalSecsDiff / 60
            val totalHoursDiff = totalMinutesDiff / 60

            Toast.makeText(requireContext(), currentTime, Toast.LENGTH_LONG).show()

            //binding.timeInputTv.text = "${(timeDiffMillis/1000) / 60} minutes"

            val dateDiff = pickedDate.split("/")

            val sdfDate = SimpleDateFormat("dd/MM/yyyy")
            val currentDate = sdfDate.format(Date()) ?: "00/00/00"
            val dateSplit = currentDate.split("/")

            val totalYearsDiff = dateSplit[2].toInt() - dateDiff[2].toInt()
            if (totalYearsDiff < 0) {
                resultString = "${abs(totalYearsDiff)} years in future"
                binding.relativeTimeTv.text = resultString
                return
            }

            val totalMonthsDiff = dateSplit[1].toInt() - dateDiff[1].toInt()
            if (totalMonthsDiff < 0) {
                resultString = "${abs(totalMonthsDiff)} months in future"
                binding.relativeTimeTv.text = resultString
                return
            }

            val totalDaysDiff = dateSplit[0].toInt() - dateDiff[0].toInt()
            if (totalDaysDiff < 0) {
                resultString = "${abs(totalDaysDiff)} days in future"
                binding.relativeTimeTv.text = resultString
                return
            }

            if (totalMinutesDiff < 0) {
                resultString = "${abs(totalMinutesDiff)} days in future"
                binding.relativeTimeTv.text = resultString
                return
            }

            binding.apply {
                if (totalYearsDiff > 0) {
                    resultString = "$totalYearsDiff year(s) ago"
                } else if (totalMonthsDiff > 0) {
                    resultString = "$totalMonthsDiff month(s) ago"
                } else if (totalDaysDiff >= 7) {
                    resultString = "${totalDaysDiff / 7} week(s) ago"
                } else if (totalDaysDiff > 0) {
                    //Toast.makeText(requireContext(), "$totalDaysDiff", Toast.LENGTH_SHORT).show()
                    resultString = "$totalDaysDiff day(s) ago"
                } else if (totalHoursDiff > 0) {
                    resultString = "$totalHoursDiff hour(s) ago"
                } else if (totalMinutesDiff > 0) {
                    resultString = "$totalMinutesDiff minute(s) ago"
                } else if (totalSecsDiff > 0) {
                    resultString = "$totalSecsDiff second(s) ago"
                } else {
                    resultString = "NULL string"
                }

                relativeTimeTv.text = resultString
            }
        }
    }

    private fun timeToMillis(hours: Int, minutes: Int, seconds: Int): Long {
        val totalMinutes = (hours * 60) + minutes
        val totalSecs = (totalMinutes * 60) + seconds
        return (totalSecs * 1000).toLong()
    }

    private fun showDatePickerDialog() {
        val materialDatePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").build()

        materialDatePicker.addOnPositiveButtonClickListener {
            it?.let {
                val date = DateFormat.format("dd/MM/yyyy", Date(it)).toString()
                binding.dateInputTv.text = date

                relativeTimeViewModel.pickedDateMillis = it
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