package id.thork.app.helper.builder.widget

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.helper.builder.adapter.LocomotifAdapter

class LocomotifLov(var activity: Activity, internal var adapter: LocomotifAdapter) :
    Dialog(activity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen) {

    internal var editSearch: AppCompatEditText? = null
    internal var recyclerView: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.locomotif_dialog)
        getWindow()?.getAttributes()?.windowAnimations = R.style.PauseDialogAnimation
        getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        editSearch = findViewById(R.id.editSearch)
        recyclerView = findViewById(R.id.recycler_view)
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = mLayoutManager
        recyclerView?.adapter = adapter

        searchListener()
    }

    private fun searchListener() {
        editSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                adapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    fun destroy() {
        this.dismiss()
    }
}