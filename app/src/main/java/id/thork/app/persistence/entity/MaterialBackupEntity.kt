package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by Raka Putra on 3/4/21
 * Jakarta, Indonesia.
 */
@Entity
class MaterialBackupEntity : BaseEntity{
    var resultCode: String? = null
    var workorderId: Int? = null
    var wonum: String? = null
    var time: String? = null
    var date: String? = null

    constructor()
    constructor(resultCode: String?, workorderId: Int?, wonum: String?, time: String?, date: String?) : super() {
        this.resultCode = resultCode
        this.workorderId = workorderId
        this.wonum = wonum
        this.time = time
        this.date = date

    }

}