package com.hane24.hoursarenotenough24.etcoption

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentEtcOptionBinding

class EtcOptionFragment : Fragment() {
    private lateinit var binding: FragmentEtcOptionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEtcOptionBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
}