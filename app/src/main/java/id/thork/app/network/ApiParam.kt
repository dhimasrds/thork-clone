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

package id.thork.app.network

object ApiParam {
    const val LOGIN_SELECT_ENDPOINT = "*";
    const val LOGIN_WHERE_ENDPOINT = "spi:maxuser{spi:loginid=";

    //workorder
    const val WORKORDER_SELECT = "*"
    const val WORKORDER_WHERE_LABORCODE_NEW = "cxlabor="
    const val WORKORDER_WHERE_LABORCODE = "assignment{laborcode="
    const val WORKORDER_WHERE_COMP = " and status in [\"COMP\",\"CLOSE\"]"
    const val WORKORDER_WHERE_STATUS = " and status in [\"APPR\",\"INPRG\",\"COMP\"]"
    

}