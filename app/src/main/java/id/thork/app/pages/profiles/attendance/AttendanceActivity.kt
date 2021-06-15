package id.thork.app.pages.profiles.attendance


import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityAttandenceBinding
import id.thork.app.pages.profiles.attendance.element.AttandanceViewModel


class AttendanceActivity : BaseActivity() {
    val TAG = AttendanceActivity::class.java.name
    private val viewModels: AttandanceViewModel by viewModels()
    private val binding: ActivityAttandenceBinding by binding(R.layout.activity_attandence)


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@AttendanceActivity
            vm = viewModels
        }


        setupToolbarWithHomeNavigation(
            getString(R.string.attendance),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )
    }


}