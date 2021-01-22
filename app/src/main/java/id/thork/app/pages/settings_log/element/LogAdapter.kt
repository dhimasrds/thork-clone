package id.thork.app.pages.settings_log.element

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.base.BaseApplication.Constants.context
import id.thork.app.base.BaseParam
import id.thork.app.pages.settings_log.LogActivity
import id.thork.app.pages.settings_log_detail.LogDetailActivity
import id.thork.app.persistence.entity.LogEntity
import id.thork.app.utils.DateUtils
import id.thork.app.utils.StringUtils

/**
 * Created by Raka Putra on 1/19/21
 * Jakarta, Indonesia.
 */
class LogAdapter(context: Context, logsEntities: Any?) : RecyclerView.Adapter<LogAdapter.LogsViewHolder>() {

    private val logsEntities = mutableListOf<LogEntity>()

    inner class LogsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var tvMessage: TextView

        fun LogsViewHolder(view: View) {
            tvMessage = view.findViewById<TextView>(R.id.tv_settings_logs)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): LogsViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_settings_logs, viewGroup, false)
        return LogsViewHolder(itemView)
    }

    override fun onBindViewHolder(languageViewHolder: LogsViewHolder, i: Int) {
        val logsEntity: LogEntity = logsEntities.get(i)
        val message: String = DateUtils.getDateTimeOB(logsEntity.createdDate).toString() + " # " + StringUtils.truncate(logsEntity.message, 90)
        languageViewHolder.tvMessage.text = message
        languageViewHolder.tvMessage.setOnClickListener {
            val intent = Intent(context, LogDetailActivity::class.java)
            intent.putExtra(BaseParam.ID, logsEntity.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return logsEntities.size
    }

}