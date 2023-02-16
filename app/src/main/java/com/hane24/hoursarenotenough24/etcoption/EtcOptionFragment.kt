package com.hane24.hoursarenotenough24.etcoption

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hane24.hoursarenotenough24.BuildConfig
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentEtcOptionBinding
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils

class EtcOptionFragment : Fragment() {
    private lateinit var binding: FragmentEtcOptionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEtcOptionBinding.inflate(layoutInflater, container, false)
        initBtn()
        return binding.root
    }

    private fun initBtn() {
        setCardBtnClick()
        setInformationBtnClick()
        setComplainBtnClick()
        setGuideBtnClick()
        setFeedbackBtnClick()
        setLogoutBtnClick()
    }

    private fun setCardBtnClick() {
        binding.etcCardLayout.setOnClickListener {
            binding.etcCardLayout.isSelected = true
        }
    }

    private fun setInformationBtnClick() {
        binding.etcInformationLayout.setOnClickListener {
            binding.etcInformationLayout.isSelected = true
            startActivity(createOpenUriIntent(PAGE_GUIDE))
        }
    }

    private fun setComplainBtnClick() {
        binding.etcComplainLayout.setOnClickListener {
            binding.etcComplainLayout.isSelected = true
            startActivity(createOpenUriIntent(INQUIRE_ATTENDANCE))
        }
    }

    private fun setGuideBtnClick() {
        binding.etcGuideLayout.setOnClickListener {
            binding.etcGuideLayout.isSelected = true
            startActivity(createOpenUriIntent(APP_GUIDE))
        }
    }

    private fun setFeedbackBtnClick() {
        binding.etcFeedbackLayout.setOnClickListener {
            binding.etcFeedbackLayout.isSelected = true
            startActivity(createOpenUriIntent(INQUIRE_MOBILE))
        }
    }

    private fun setLogoutBtnClick() {
        binding.etcLogoutLayout.setOnClickListener {
            binding.etcLogoutLayout.isSelected = true
            logOutOnClick()
        }
    }

    private fun logOutOnClick() {
        SharedPreferenceUtils.saveAccessToken("")

        val intent = Intent(requireActivity(), LoginActivity::class.java)
            .putExtra("loginState", State.LOGIN_FAIL)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent).also { requireActivity().finish() }
    }
    private fun createOpenUriIntent(url: String): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

    companion object {
        private const val APP_GUIDE = BuildConfig.APP_GUIDE
        private const val PAGE_GUIDE = BuildConfig.PAGE_GUIDE
        private const val INQUIRE_ATTENDANCE = BuildConfig.INQUIRY_ATTENDANCE
        private const val INQUIRE_MOBILE = BuildConfig.INQUIRY_MOBILE
        private const val NUM_PAGES = 2
    }
}