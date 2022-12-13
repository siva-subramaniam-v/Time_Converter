package com.example.timeconverter.ui.viewmodels

import androidx.lifecycle.ViewModel

class RelativeTimeViewModel: ViewModel() {
    var pickedDate = ""
    var pickedHours = -1
    var pickedMinutes = -1

    var hourToday = -1
    var minuteToday = -1
    var secondsToday = -1
}