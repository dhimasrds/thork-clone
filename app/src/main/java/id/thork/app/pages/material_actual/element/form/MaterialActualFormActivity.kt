package id.thork.app.pages.material_actual.element.form

import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.activity.viewModels
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityMaterialActualFormBinding


class MaterialActualFormActivity : BaseActivity() {
    val TAG = MaterialActualFormActivity::class.java.name

    val viewModel: MaterialActualFormViewModel by viewModels()
    private val binding: ActivityMaterialActualFormBinding by binding(R.layout.activity_material_actual_form)

    override fun setupView() {
        super.setupView()

        binding.apply {
            lifecycleOwner = this@MaterialActualFormActivity
            vm = viewModel
        }
    }

    fun onItemTypeClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_item ->
                    if (checked) {

                    }
                R.id.radio_material ->
                    if (checked) {

                    }
            }
        }
    }

    fun onIssueTypeCheckec(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.checkbox_direct -> {
                    if (checked) {

                    } else {

                    }
                }
            }
        }
    }


}