package id.thork.app.pages.profiles.profile

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityProfilesBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.main.MainActivity
import id.thork.app.pages.profiles.about_us.AboutActivity
import id.thork.app.pages.profiles.attendance.AttandanceActivity
import id.thork.app.pages.profiles.help_center.HelpCenterActivity
import id.thork.app.pages.profiles.profile.element.ProfileViewModel
import id.thork.app.pages.profiles.setting.settings.SettingsActivity
import id.thork.app.pages.server.ServerActivity
import id.thork.app.utils.LocaleHelper

/**
 * Created by Raka Putra on 6/11/21
 * Jakarta, Indonesia.
 */
class ProfileActivity : BaseActivity(), CustomDialogUtils.DialogActionListener {
    private val viewModel: ProfileViewModel by viewModels()
    private val binding: ActivityProfilesBinding by binding(R.layout.activity_profiles)

    private lateinit var customDialogUtils: CustomDialogUtils

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@ProfileActivity
            vm = viewModel
        }
        customDialogUtils = CustomDialogUtils(this)

        setupToolbarWithHomeNavigation(
            getString(R.string.action_profile),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )

        handlerOnclick()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.validateUsername()
        viewModel.username.observe(this, Observer {
            binding.usernameProfile.text = it
        })
        viewModel.logout.observe(this, {
            if (it == BaseParam.APP_TRUE) {
                goToServerAcitivity()
            }
        })
    }

    private fun handlerOnclick() {

        binding.attendance.setOnClickListener {
            goToAttendance()
        }

        binding.settings.setOnClickListener {
            goToSetting()
        }

        binding.helpCenter.setOnClickListener {
            goToHelpCenter()
        }

        binding.aboutUs.setOnClickListener {
            goToAboutActivity()
        }

        binding.buttonLogout.setOnClickListener {
            setDialogLogout()
        }
    }

    private fun goToAttendance() {
        startActivity(Intent(this, AttandanceActivity::class.java))
    }

    private fun goToSetting() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun goToHelpCenter() {
        startActivity(Intent(this, HelpCenterActivity::class.java))
    }

    private fun goToAboutActivity() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    private fun goToServerAcitivity() {
        startActivity(Intent(this, ServerActivity::class.java))
        finish()
    }

    private fun setDialogLogout() {
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.logout_title)
            .setDescription(R.string.logout_qustion)
            .setListener(this)
        customDialogUtils.show()
    }


    override fun onRightButton() {
        LocaleHelper.setLocale(this, BaseParam.APP_DEFAULT_LANG)
        viewModel.logout()
    }

    override fun onLeftButton() {
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToPreviousActivity()
    }

    override fun goToPreviousActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        //finish()
        super.goToPreviousActivity()
    }
}