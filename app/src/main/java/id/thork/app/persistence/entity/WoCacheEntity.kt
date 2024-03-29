package id.thork.app.persistence.entity

import android.os.Parcelable
import io.objectbox.annotation.Entity
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Created by Dhimas Saputra on 08/01/21
 * Jakarta, Indonesia.
 */
@Parcelize
@Entity
data class WoCacheEntity(
    var woId : Int? = null,
    var syncStatus : Int? = null,
    var isChanged : Int? = null,
    var isLatest : Int? = null,
    var wonum : String? = null,
    var syncBody : String? = null,
    var syncUpdate : String? = null,
    var laborCode : String? = null,
    var status : String? = null,
    var latitude : Double? = null,
    var longitude: Double? = null,
    var changeDate: String? = null,
    var changeDateLocal: Date? = null,
    var reportDateUTCTime: Date? = null,
    var reportString: String? = null,
    var externalREFID: String? = null
) :BaseEntity(), Parcelable{

}