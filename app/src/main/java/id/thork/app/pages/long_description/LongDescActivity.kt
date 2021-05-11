package id.thork.app.pages.long_description

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.google.gson.Gson
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityLongDescBinding
import id.thork.app.network.response.work_order.Longdescription
import id.thork.app.network.response.work_order.Member
import id.thork.app.pages.long_description.element.LongDescViewModel
import id.thork.app.persistence.entity.WoCacheEntity
import timber.log.Timber
import java.util.*

/**
 * Created by Raka Putra on 3/8/21
 * Jakarta, Indonesia.
 */
class LongDescActivity : BaseActivity() {
    private val TAG = LongDescActivity::class.java.name
    private val TAG_CREATE = "TAG_CREATE"

    private val viewModel: LongDescViewModel by viewModels()
    private val binding: ActivityLongDescBinding by binding(R.layout.activity_long_desc)

    private var valueLongdesc: String? = null
    private var intentLongdesc: String? = null
    private var textLongdesc: String? = null
    private var intentTagCreateWo: String? = null
    private var intentStatus: String? = null
    private var intentWoId: String? = null
    private var intentWonum: String? = null
    private var username: String? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@LongDescActivity
            vm = viewModel
        }
        setupToolbarWithHomeNavigation(
            getString(R.string.note),
            navigation = false,
            filter = false,
            scannerIcon = false
        )
        retrieveFromIntent()
        initView()
    }

    override fun setupListener() {
        super.setupListener()
        binding.save.setOnClickListener {
            saveNote()
            val intent = Intent()
            intent.putExtra("longdesc", intentLongdesc)
            intent.putExtra(BaseParam.WONUM, intentWonum)
            setResult(RESULT_OK, intent)
            finish()
            Timber.d("wodetailCreatelongdesc : %s", intentLongdesc)
        }

        binding.cancel.setOnClickListener { finish() }
    }

    private fun saveNote() {
        //save from create workorder
        if (intentTagCreateWo != null) {
            Timber.d("tagcreate : %s", TAG_CREATE)
            intentLongdesc = binding.longdesc.text.toString()

            val member = Member()
            member.description_longdescription = intentLongdesc

            val tWoCacheEntity = WoCacheEntity()
            tWoCacheEntity.syncBody = convertToJson(member)
        } else {
            saveNoteFromWoDetail()
        }
    }

    private fun initView() {
        //show note from create WO
        if (intentTagCreateWo != null) {
            binding.longdesc.setText(textLongdesc)
        } else if (intentStatus.equals(BaseParam.COMPLETED)) {
            //show note from Wo detail Complete
            showWoFromDetailNotComplete()

            binding.longdesc.isEnabled = false
            binding.save.visibility = View.INVISIBLE
            binding.cancel.visibility = View.INVISIBLE
        } else if (!intentStatus.equals(BaseParam.COMPLETED)) {
            //show note from Wo detail not Complete
            showWoFromDetailNotComplete()
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.validateUsername()
        viewModel.username.observe(this, androidx.lifecycle.Observer {
            username = it
        })
    }

    private fun retrieveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
        intentStatus = intent.getStringExtra(BaseParam.STATUS)
        intentWoId = intent.getStringExtra(BaseParam.WORKORDERID)
        intentTagCreateWo = intent.getStringExtra(TAG_CREATE)
        textLongdesc = intent.getStringExtra("TEXT_LONGDESC")
        if (intentWonum != null) {
            viewModel.fetchWobyWonum(intentWonum!!)
        }
    }

    private fun saveNoteFromWoDetail() {
        intentWonum.whatIfNotNullOrEmpty { intentWonum ->
            val currentWoCache = viewModel.findWobyWonum(intentWonum)
            intentLongdesc = binding.longdesc.text.toString()

            val ld = Longdescription()
            ld.ldtext = intentLongdesc
            val longdescriptions: MutableList<Longdescription> = ArrayList()
            longdescriptions.add(ld)

            val moshi = Moshi.Builder().build()
            val memberJsonAdapter: JsonAdapter<Member> = moshi.adapter<Member>(
                Member::class.java
            )
            currentWoCache.whatIfNotNull { woCache ->
                woCache.syncBody.whatIfNotNullOrEmpty { woCacheSyncBody ->
                    val currentMember = memberJsonAdapter.fromJson(woCacheSyncBody)
                    currentMember.whatIfNotNull { memberCache ->
                        memberCache.longdescription = longdescriptions
                        memberCache.description_longdescription = intentLongdesc

                        woCache.syncBody = memberJsonAdapter.toJson(memberCache)
                        woCache.wonum = memberCache.wonum
                        viewModel.updateWo(woCache, username!!)
                    }
                }
            }
        }
    }

    private fun showWoFromDetailNotComplete() {
        val currentWoCache = viewModel.findWobyWonum(intentWonum!!)
        intentLongdesc = binding.longdesc.text.toString()
        val moshi = Moshi.Builder().build()
        val memberJsonAdapter: JsonAdapter<Member> = moshi.adapter<Member>(
            Member::class.java
        )
        val currentMember = memberJsonAdapter.fromJson(currentWoCache!!.syncBody)
        if (currentMember!!.longdescription != null) {
            valueLongdesc = currentMember.longdescription?.get(0)?.ldtext
            binding.longdesc.setText(valueLongdesc)
        }
    }

    private fun convertToJson(member: Member): String? {
        val gson = Gson()
        return gson.toJson(member)
    }

    override fun goToPreviousActivity() {
        finish()
    }

}