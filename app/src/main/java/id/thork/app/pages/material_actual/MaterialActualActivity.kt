/*
 * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * This.ID. ("Confidential Information").
 *
 * Such Confidential Information shall not be disclosed and shall
 * use it only	 in accordance with the terms of the license agreement
 * entered into with This.ID; other than in accordance with the written
 * permission of This.ID.
 */

package id.thork.app.pages.material_actual

import android.content.Intent
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.skydoves.whatif.whatIfNotNull
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityMaterialActualBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.material_actual.element.MaterialActualAdapter
import id.thork.app.pages.material_actual.element.MaterialActualViewModel
import id.thork.app.pages.material_actual.element.form.MaterialActualFormActivity
import id.thork.app.pages.material_plan.element.form.MaterialPlanFormActivity
import id.thork.app.persistence.entity.MaterialActualEntity
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MaterialActualActivity : BaseActivity(), MaterialActualAdapter.MaterialActualAdapterItemClickListener {
    val TAG = MaterialActualActivity::class.java.name

    val viewModel: MaterialActualViewModel by viewModels()
    private val binding: ActivityMaterialActualBinding by binding(R.layout.activity_material_actual)

    private lateinit var materialActualAdapter: MaterialActualAdapter
    private lateinit var materialActualEntityList: MutableList<MaterialActualEntity>

    private var intentWoId = 0
    private var status: String? = null
    private var intentState: String? = BaseParam.FORM_STATE_READ_ONLY

    @Inject
    @Named("svgRequestOption")
    lateinit var svgRequestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun setupView() {
        super.setupView()
        materialActualEntityList = mutableListOf()
        materialActualAdapter =
            MaterialActualAdapter(this, preferenceManager, svgRequestOptions, materialActualEntityList, this)

        binding.apply {
            lifecycleOwner = this@MaterialActualActivity
            vm = viewModel

            rvMaterials.apply {
                layoutManager = LinearLayoutManager(this@MaterialActualActivity)
                addItemDecoration(
                    DividerItemDecoration(
                        this@MaterialActualActivity,
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = materialActualAdapter
                setUpFilterListener()
            }
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.wo_material_actual),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )
        retrieveFromIntent()
        validationView()
    }

    fun validationView() {
        if (status.equals(BaseParam.CLOSED)) {
            binding.btnAdd.visibility = View.GONE
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, MaterialActualFormActivity::class.java)
            intent.putExtra(BaseParam.WORKORDERID, intentWoId)
            intent.putExtra(BaseParam.FORM_STATE, BaseParam.FORM_STATE_NEW)
            startActivity(intent)
            finish()
        }
    }

    private fun retrieveFromIntent() {
        intentWoId = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        intentState = intent.getStringExtra(BaseParam.FORM_STATE)
        status = intent.getStringExtra(BaseParam.STATUS)
        Timber.d("retrieveFromIntent() intentWoId: %s", intentWoId)
        viewModel.initListMaterialActual(intentWoId.toString())
        intentState.whatIfNotNull {
            if (it.equals(BaseParam.FORM_STATE_READ_ONLY)) {
                binding.btnLayout.visibility = View.GONE
            }
        }
        viewModel.initListMaterialActual(intentWoId.toString())
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.listMaterial.observe(this, Observer {
            materialActualEntityList.clear()
            materialActualEntityList.addAll(it)
            materialActualAdapter.notifyDataSetChanged()
        })

        viewModel.result.observe(this, Observer {
            viewModel.initListMaterialActual(intentWoId.toString())
        })
    }

    private fun setUpFilterListener() {
        binding.apply {
            etFindMaterial.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Timber.d("setUpFilterListener() TextChange : %s", newText)
                    materialActualAdapter.filter.filter(newText)
                    return false
                }
            })
        }
    }

    override fun onClickItem(materialActualEntity: MaterialActualEntity) {
        Timber.tag(TAG).d("onClickItem() woId: %s id: %s", materialActualEntity.workorderId,
            materialActualEntity.id)
        val intent = Intent(this, MaterialActualFormActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, materialActualEntity.workorderId)
        intent.putExtra(BaseParam.ID, materialActualEntity.id)
        if (intentState.equals(BaseParam.FORM_STATE_NEW)) {
            intentState = BaseParam.FORM_STATE_EDIT
        }
        intent.putExtra(BaseParam.FORM_STATE, intentState)
        startActivity(intent)
        finish()
    }
}