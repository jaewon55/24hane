package com.hane24.hoursarenotenough24.etcoption

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.TransitionInflater
import com.hane24.hoursarenotenough24.BuildConfig
import com.hane24.hoursarenotenough24.MainActivity
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.database.createDatabase
import com.hane24.hoursarenotenough24.databinding.FragmentEtcOptionBinding
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.BASE_URL
import com.hane24.hoursarenotenough24.reissue.ReissueFragment
import com.hane24.hoursarenotenough24.repository.TimeRepository
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Time

class EtcOptionFragment : Fragment() {
    private lateinit var binding: FragmentEtcOptionBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEtcOptionBinding.inflate(layoutInflater, container, false)
        initBtn()
        swipeRefreshLayout = (requireActivity() as MainActivity).findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.isEnabled = false
        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        swipeRefreshLayout.isEnabled = true
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
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, ReissueFragment())
                .commit()
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
        CoroutineScope(Dispatchers.IO).launch {
            TimeRepository(createDatabase() as TimeDatabase).deleteAll()
        }

        val intent = Intent(requireActivity(), LoginActivity::class.java)
            .putExtra("loginState", State.LOGIN_FAIL)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent).also { requireActivity().finish() }
    }
    private fun createOpenUriIntent(url: String): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

    companion object {
        private const val APP_GUIDE = BASE_URL + "redirect/usage"
        private const val PAGE_GUIDE = BASE_URL + "redirect/money_guidelines"
        private const val INQUIRE_ATTENDANCE = BASE_URL + "redirect/question"
        private const val INQUIRE_MOBILE = BASE_URL + "redirect/feedback"
    }
}