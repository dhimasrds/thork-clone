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

package id.thork.app.pages.material_plan

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
import id.thork.app.databinding.ActivityMaterialPlanBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.material_plan.element.MaterialPlanAdapter
import id.thork.app.pages.material_plan.element.MaterialPlanViewModel
import id.thork.app.pages.material_plan.element.form.MaterialPlanFormActivity
import id.thork.app.persistence.entity.WpmaterialEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MaterialPlanActivity : BaseActivity(), MaterialPlanAdapter.MaterialPlanAdapterItemClickListener {
    val TAG = MaterialPlanActivity::class.java.name

    val viewModel: MaterialPlanViewModel by viewModels()
    private val binding: ActivityMaterialPlanBinding by binding(R.layout.activity_material_plan)

    private lateinit var materialPlanAdapter: MaterialPlanAdapter
    private lateinit var wpmaterialList: MutableList<WpmaterialEntity>

    private var intentWoId = 0
    private var intentState: String? = BaseParam.FORM_STATE_READ_ONLY

    @Inject
    @Named("svgRequestOption")
    lateinit var svgRequestOptions: RequestOptions

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun setupView() {
        super.setupView()
        wpmaterialList = mutableListOf()
        materialPlanAdapter =
            MaterialPlanAdapter(this, preferenceManager, svgRequestOptions, wpmaterialList, this)

        binding.apply {
            lifecycleOwner = this@MaterialPlanActivity
            vm = viewModel

            rvMaterials.apply {
                layoutManager = LinearLayoutManager(this@MaterialPlanActivity)
                addItemDecoration(
                    DividerItemDecoration(
                        this@MaterialPlanActivity,
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = materialPlanAdapter
                setUpFilterListener()
            }
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.wo_material_plan),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )

        retrieveFromIntent()
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, MaterialPlanFormActivity::class.java)
            intent.putExtra(BaseParam.WORKORDERID, intentWoId)
            intent.putExtra(BaseParam.FORM_STATE, BaseParam.FORM_STATE_NEW)
            startActivity(intent)
            finish()
        }
    }

    private fun retrieveFromIntent() {
        intentWoId = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        intentState = intent.getStringExtra(BaseParam.FORM_STATE)
        Timber.d("retrieveFromIntent() intentWoId: %s", intentWoId)
        viewModel.initListMaterialPlan(intentWoId.toString())
        intentState.whatIfNotNull {
            if (it.equals(BaseParam.FORM_STATE_READ_ONLY)) {
                binding.btnLayout.visibility = View.GONE
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.listMaterial.observe(this, Observer {
            wpmaterialList.clear()
            wpmaterialList.addAll(it)
            Timber.tag(TAG).d("setupObserver() listMaterial: %s", wpmaterialList)
            materialPlanAdapter.notifyDataSetChanged()
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
                    materialPlanAdapter.filter.filter(newText)
                    return false
                }
            })
        }
    }

    override fun onClickItem(wpmaterialEntity: WpmaterialEntity) {
        Timber.tag(TAG).d("onClickItem() woId: %s id: %s", wpmaterialEntity.workorderId,
        wpmaterialEntity.id)
        val intent = Intent(this, MaterialPlanFormActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, wpmaterialEntity.workorderId)
        intent.putExtra(BaseParam.ID, wpmaterialEntity.id)
        if (intentState.equals(BaseParam.FORM_STATE_NEW)) {
            intentState = BaseParam.FORM_STATE_EDIT
        }
        intent.putExtra(BaseParam.FORM_STATE, intentState)
        startActivity(intent)
        finish()
    }
}