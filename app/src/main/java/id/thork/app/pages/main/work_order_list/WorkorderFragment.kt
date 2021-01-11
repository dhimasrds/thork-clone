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

package id.thork.app.pages.main.work_order_list

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.thork.app.R
import id.thork.app.databinding.ActivityLoginBinding
import id.thork.app.databinding.FragmentWorkorderBinding
import id.thork.app.pages.main.element.WorkOrderAdapter
import id.thork.app.persistence.entity.WoEntity
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class WorkorderFragment : Fragment() {
    private val arrayList = ArrayList<WoEntity>()
    private lateinit var binding : FragmentWorkorderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         binding = FragmentWorkorderBinding.inflate(inflater,container, false)

        for (i in 1..20){
            val wo = WoEntity(
                "random"
            )
            wo.wonum = "125$i"
            wo.status = "description $i"
            wo.laborCode = "alfamart kota $i"
            arrayList.add(wo)
        }
        val myAdapter =WorkOrderAdapter(arrayList)

        Timber.d("list size :%s", arrayList.size)

        binding.recyclerView.apply {
            binding.recyclerView.adapter = myAdapter
        }

        return binding.root
    }
}