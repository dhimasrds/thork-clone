package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by Raka Putra on 3/4/21
 * Jakarta, Indonesia.
 */
@Entity
class MaterialEntity : BaseEntity{
    var resultCode: String? = null
    var workorderId: Int? = null
    var wonum: Int? = null
    var time: String? = null
    var date: String? = null

    constructor()
    constructor(resultCode: String?, workorderId: Int?, wonum: Int?, time: String?, date: String?) : super() {
        this.resultCode = resultCode
        this.workorderId = workorderId
        this.wonum = wonum
        this.time = time
        this.date = date

    }

}