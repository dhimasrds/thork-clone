package id.thork.app.pages.task

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityTaskBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.task.element.TaskAdapter
import id.thork.app.pages.task.element.TaskViewModel
import id.thork.app.persistence.entity.TaskEntity
import id.thork.app.utils.DateUtils
import timber.log.Timber
import java.util.*

/**
 * Created by Raka Putra on 6/23/21
 * Jakarta, Indonesia.
 */
class TaskActivity : BaseActivity(), CustomDialogUtils.DialogActionListener {
    val TAG = TaskActivity::class.java.name
    private val viewModels: TaskViewModel by viewModels()
    private val binding: ActivityTaskBinding by binding(R.layout.activity_task)

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskEntity: MutableList<TaskEntity>
    private lateinit var customDialogUtils: CustomDialogUtils

    private var intentWonum: String? = null
    private var intentStatus: String? = null
    private var intentTag: String? = null
    private var intentWoid: Int? = null
    private var intentWoidTask: Int? = null
    private var idTask: Int = 10

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
        customDialogUtils = CustomDialogUtils(this)

        taskEntity = mutableListOf()
        taskAdapter = TaskAdapter(this, taskEntity)
        binding.rvTask.adapter = taskAdapter
        retrieveFromIntent()

    }

    private fun validateTaskId(valueTaskId: Int) {
        valueTaskId.whatIfNotNull {
            idTask = valueTaskId + 10
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        intentTag.whatIfNotNull(
            whatIf = {
                viewModels.listTaskCreateWo.observe(this, Observer {
                    taskEntity.clear()
                    taskEntity.addAll(it)
                    taskAdapter.notifyDataSetChanged()
                    val lattestTaskId = taskEntity[it.size - 1].taskId
                    lattestTaskId.whatIfNotNull { taskid ->
                        validateTaskId(taskid)
                    }
                })
            },
            whatIfNot = {
                viewModels.listTask.observe(this, Observer {
                    taskEntity.clear()
                    taskEntity.addAll(it)
                    taskAdapter.notifyDataSetChanged()
                    val lattestTaskId = taskEntity[it.size - 1].taskId
                    lattestTaskId.whatIfNotNull { taskid ->
                        validateTaskId(taskid)
                    }
                }
                )
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
            val currentDate = Date()
            val currentDateString = DateUtils.getAppDateFormat(currentDate)
            val currentTimeString = DateUtils.getAppTimeFormat(currentDate)
            Timber.tag(TAG).d("setupListener() date String: %s", currentDateString)
            intent.putExtra(BaseParam.SHEDULE_START, currentDateString)
            intent.putExtra(BaseParam.SHEDULE_START_TIME, currentTimeString)
            intentTag.whatIfNotNull {
                intent.putExtra(BaseParam.TAG_TASK, it)
            }
            startActivity(intent)
        }
    }

    private fun retrieveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentWoid = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        intentStatus = intent.getStringExtra(BaseParam.STATUS)
        intentTag = intent.getStringExtra(BaseParam.TAG_TASK)
        intentWoid.whatIfNotNull {
            intentWonum.whatIfNotNull { wonum ->
                if (intentTag != null) {
                    viewModels.initTaskCreateWo(wonum)
                } else {
                    viewModels.initTask(it)
                }
            }
        }
    }

    fun setDialogDeleteTask(woidTask: Int) {
        intentWoidTask = woidTask
        customDialogUtils.setLeftButtonText(R.string.dialog_no)
            .setRightButtonText(R.string.dialog_yes)
            .setTittle(R.string.task_title)
            .setDescription(R.string.task_qustion)
            .setListener(this)
        customDialogUtils.show()
    }

    override fun onRightButton() {
        intentWoidTask.whatIfNotNull {
            viewModels.deleteTask(it)
            finish()
        }
    }

    override fun onLeftButton() {
        customDialogUtils.dismiss()
    }

    override fun onMiddleButton() {
        customDialogUtils.dismiss()
    }
}