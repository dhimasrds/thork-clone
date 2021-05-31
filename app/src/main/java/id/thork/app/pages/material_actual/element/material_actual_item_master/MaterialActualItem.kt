package id.thork.app.pages.material_actual.element.material_actual_item_master

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityMaterialActualItemBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.entity.MaterialEntity
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MaterialActualItem : BaseActivity() {
    val TAG = MaterialActualItem::class.java.name

    val viewModel: MaterialActualItemViewModel by viewModels()
    private val binding: ActivityMaterialActualItemBinding by binding(R.layout.activity_material_actual_item)

    private lateinit var materialActualItemAdapter: MaterialActualItemAdapter
    private lateinit var materialEntities: MutableList<MaterialEntity>

    @Inject
    @Named("svgRequestOption")
    lateinit var svgRequestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun setupView() {
        super.setupView()
        materialEntities = mutableListOf()
        materialActualItemAdapter =
            MaterialActualItemAdapter(this, preferenceManager, svgRequestOptions, materialEntities, this)

        binding.apply {
            lifecycleOwner = this@MaterialActualItem
            vm = viewModel
            rvMaterials.apply {
                layoutManager = LinearLayoutManager(this@MaterialActualItem)
                addItemDecoration(
                    DividerItemDecoration(
                        this@MaterialActualItem,
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = materialActualItemAdapter
            }
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.wo_material_actual),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )
        viewModel.initListMaterial()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.listMaterial.observe(this, Observer {
            materialEntities.clear()
            materialEntities.addAll(it)
            materialActualItemAdapter.notifyDataSetChanged()
        })
    }


}