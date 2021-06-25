package id.thork.app.pages.task

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityTaskBinding
import id.thork.app.pages.task.element.TaskViewModel
import id.thork.app.persistence.entity.TaskEntity
import timber.log.Timber

/**
 * Created by Raka Putra on 6/23/21
 * Jakarta, Indonesia.
 */
class TaskActivity : BaseActivity() {
    val TAG = TaskActivity::class.java.name
    private val viewModels: TaskViewModel by viewModels()
    private val binding: ActivityTaskBinding by binding(R.layout.activity_task)
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskEntity: MutableList<TaskEntity>
    private var intentWonum: String? = null
    private var intentStatus: String? = null
    private var intentWoid: Int? = null
    private var idTask: Int = 20

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@TaskActivity
            vm = viewModels
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.task_toolbar),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
        )

        taskEntity = mutableListOf()
        taskAdapter = TaskAdapter(taskEntity)
        binding.rvTask.adapter = taskAdapter
        retrieveFromIntent()
//        validateTaskId(intentWoid!!)

    }

    private fun validateTaskId(woid: Int) {
        var a = viewModels.findListTaskByTaskId(woid)
        if (a != null){
            idTask = a + 10
            Timber.d("raka %s ", a)
        } else {
            a = 0
            idTask = a + 10
            Timber.d("raka %s ", a)
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModels.listTask.observe(this, Observer {
            taskEntity.clear()
            taskEntity.addAll(it)
            taskAdapter.notifyDataSetChanged()
        })
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnCreateTask.setOnClickListener {
            val intent = Intent(this, CreateTaskActivity::class.java)
            intent.putExtra(BaseParam.WORKORDERID, intentWoid)
            intent.putExtra(BaseParam.WONUM, intentWonum)
            intent.putExtra(BaseParam.STATUS, intentStatus)
            intent.putExtra(BaseParam.TASKID, idTask)
            startActivity(intent)
        }
    }

    private fun retrieveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWoid = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        intentStatus = intent.getStringExtra(BaseParam.STATUS)
        intentWoid.whatIfNotNull {
            viewModels.initTask(it)
        }
    }
}