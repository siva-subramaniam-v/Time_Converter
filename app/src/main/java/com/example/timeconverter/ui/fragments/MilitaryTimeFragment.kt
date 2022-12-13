package com.example.timeconverter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.timeconverter.R
import com.example.timeconverter.databinding.FragmentMilitaryTimeBinding

class MilitaryTimeFragment: Fragment() {
    private lateinit var binding: FragmentMilitaryTimeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMilitaryTimeBinding.inflate(inflater)
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
       binding.convertBtn.setOnClickListener{
           val inputTime = "${binding.normalTimeEt.text}"

           if (inputTime.isNotBlank()) {
               binding.militaryTimeTv.text = "${convertTime(inputTime)} hours"
           } else {
               Toast.makeText(requireContext(), "Please input a time", Toast.LENGTH_SHORT).show()
           }
       }

        binding.nextProblemBtn.setOnClickListener {
            findNavController().navigate(R.id.relativeTimeFragment)
        }
    }

    private fun convertTime(inputTime: String): String {
        val timeSplit = inputTime.split(":")
        val meridian = timeSplit[2].substring(2)

        val normalTime = "${timeSplit[1]}:${timeSplit[2].subSequence(0,2)}"

        val hour = if (meridian == "AM" ) {
            if (timeSplit[0] == "12") "00" else timeSplit[0]
        } else {
            if (timeSplit[0] != "12") "${timeSplit[0].toInt()+12}" else "12"
        }

        return "$hour:$normalTime"
    }
}