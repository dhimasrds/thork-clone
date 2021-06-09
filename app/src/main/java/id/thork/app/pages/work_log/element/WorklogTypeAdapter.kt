package id.thork.app.pages.work_log.element

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardviewListWorklogBinding
import id.thork.app.pages.find_asset_location.element.FindAssetAdapter
import id.thork.app.pages.work_log.create_work_log.CreateWorkLogActivity
import id.thork.app.pages.work_log.type.WorkLogTypeActivity
import id.thork.app.persistence.entity.WorklogTypeEntity

/**
 * Created by Dhimas Saputra on 07/06/21
 * Jakarta, Indonesia.
 */
class WorklogTypeAdapter  constructor(
    private val worklogTypeEntity: List<WorklogTypeEntity>,
    private val activity: WorkLogTypeActivity

) :
    RecyclerView.Adapter<WorklogTypeAdapter.ViewHolder>() {
    val TAG = FindAssetAdapter::class.java.name


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardviewListWorklogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(val binding: CardviewListWorklogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(worklogEntity: WorklogTypeEntity) {
            binding.apply {
                tvDate.visibility = View.INVISIBLE
                tvType.visibility = View.INVISIBLE
                tvTitle.text = worklogEntity.type
                tvDesc.text = worklogEntity.description
                cardWorklog.setOnClickListener {
                    val intent = Intent(BaseApplication.context, CreateWorkLogActivity::class.java)
                    intent.putExtra(BaseParam.WORKLOGTYPE, worklogEntity.type)
                    activity.setResult(AppCompatActivity.RESULT_OK, intent)
                    activity.finish()
                }
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val worklogEntity: WorklogTypeEntity = worklogTypeEntity[position]
        holder.bind(worklogEntity)
    }

    override fun getItemCount(): Int {
        return worklogTypeEntity.size
    }


}
