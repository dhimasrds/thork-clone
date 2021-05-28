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

package id.thork.app.pages.material_plan.element.material_plan_list

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.pages.material_plan.element.MaterialPlanViewModel

class MaterialPlanItemViewModel @ViewModelInject constructor(
    private val context: Context
) : LiveCoroutinesViewModel() {
    val TAG = MaterialPlanItemViewModel::class.java.name

}