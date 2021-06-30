package id.thork.app.persistence.entity

import android.os.Parcelable
import io.objectbox.annotation.Entity
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Created by Raka Putra on 6/23/21
 * Jakarta, Indonesia.
 */
@Parcelize
@Entity
class TaskEntity(
    var woId : Int? = null,
    var wonum : String? = null,
    var taskId : Int? = null,
    var desc : String? = null,
    var scheduleStart : Date? = null,
    var actualStart : Date? = null,
    var estDuration : Double? = null,
    var status : String? = null
    ) : BaseEntity(), Parcelable {
}