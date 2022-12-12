package com.example.timeconverter.ui.viewmodels

import androidx.lifecycle.ViewModel

class RelativeTimeViewModel: ViewModel() {
    var pickedDateMillis = -1L
    var pickedDate = ""

    var pickedHours = -1
    var pickedMinutes = -1
}