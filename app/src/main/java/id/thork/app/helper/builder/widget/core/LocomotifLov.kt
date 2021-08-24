package id.thork.app.helper.builder.widget.core

import android.app.Activity
import android.app.Dialog
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.helper.builder.adapter.LocomotifAdapter

class LocomotifLov(var activity: Activity, internal var adapter: LocomotifAdapter, internal var title: String) :
    Dialog(activity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen) {

    internal var toolBar: Toolbar? = null
    internal var toolBarTitle: TextView? = null
    internal var editTextToolbar: EditText? = null
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
        toolBar = findViewById(R.id.app_toolbar)
        toolBar?.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)

        toolBarTitle = findViewById(R.id.toolbar_title)
        toolBarTitle?.setText(title)
        editTextToolbar = findViewById(R.id.toolbartext)
        editTextToolbar?.visibility = View.GONE

        editSearch = findViewById(R.id.editSearch)
        recyclerView = findViewById(R.id.recycler_view)
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = mLayoutManager
        recyclerView?.adapter = adapter

        searchListener()
        toolBarListener()
    }

    private fun toolBarListener() {
        toolBar?.setNavigationOnClickListener {
            this.dismiss()
        }
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