package id.thork.app.pages.main.work_order_list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.baoyz.widget.PullRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.databinding.FragmentWorkOrderListBinding
import id.thork.app.pages.main.element.WoLoadStateAdapter
import id.thork.app.pages.main.element.WorkOrderAdapter
import id.thork.app.pages.main.element.WorkOrderListViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class WorkOrderListFragment : Fragment() {
    private lateinit var workOrderAdapter: WorkOrderAdapter
    private val viewModel: WorkOrderListViewModel by viewModels()
    private lateinit var binding: FragmentWorkOrderListBinding
    private lateinit var pullRefreshLayout: PullRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorkOrderListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        pullRefreshLayout = binding.swipeRefreshLayout
        workOrderAdapter = WorkOrderAdapter()



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
        setUpFilterListener()
        swipeRefresh()
//        progressBarOnFirstLoad()

    }

    private fun setupView() {
        binding.recyclerView.apply {
            adapter = workOrderAdapter.withLoadStateFooter(
                footer = WoLoadStateAdapter { workOrderAdapter.retry() }
            )
        }

    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.woList.observe(viewLifecycleOwner) {
                workOrderAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                Timber.d("onCreateView :%s", it)
            }
        }
    }

    private fun swipeRefresh(){
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL)
        pullRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.blueTextStatus),
            ContextCompat.getColor(requireContext(), R.color.colorYellow),
            ContextCompat.getColor(requireContext(), R.color.colorGreen)
        )

        pullRefreshLayout.setOnRefreshListener {
            workOrderAdapter.refresh()
            workOrderAdapter.addLoadStateListener { loadstate ->
                Timber.d("loadresult wo :%s",loadstate.refresh)
                if (loadstate.refresh !is LoadState.Loading){
                    pullRefreshLayout.setRefreshing(false)
                }
            }
        }
    }

    private fun progressBarOnFirstLoad() {
        binding.apply {
            btnRetry.setOnClickListener {
                workOrderAdapter.retry()
            }
            // show the loading state for te first load
            workOrderAdapter.addLoadStateListener { loadState ->

                if (loadState.refresh is LoadState.Loading) {

                    btnRetry.visibility = View.GONE

                    // Show ProgressBar
                    progressBar.visibility = View.VISIBLE
                } else {
                    // Hide ProgressBar
                    progressBar.visibility = View.GONE
                    Timber.d("progressBarOnFirstLoad append:%s", loadState.append)
                    Timber.d("progressBarOnFirstLoad prepend:%s", loadState.prepend)
                    Timber.d("progressBarOnFirstLoad refresh:%s", loadState.refresh)
                    // If we have an error, show a toast
                    val errorState = when {
                        loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                        loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                        loadState.refresh is LoadState.Error  -> {
                            Timber.d("btnretry :%s", "sukses")
                            btnRetry.visibility = View.VISIBLE
                            loadState.refresh as LoadState.Error
                        }

                        else -> null
                    }
                    errorState?.let {
                        val toast = Toast.makeText(
                            requireContext(),
                            "Please, check your connection",
                            Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
                }
            }
        }
    }

    private fun setUpFilterListener() {
        binding.apply {
            editText.addTextChangedListener(object : TextWatcher {
                private var timer = Timer()
                private val DELAY: Long = 1000 // milliseconds
                var isTyping = false
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //Method before text change
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    //Method on text Change
                }

                override fun afterTextChanged(s: Editable) {
                    if (!isTyping) {
                        // Send notification for start typing event
                        isTyping = true
                    }
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                isTyping = false
                                activity?.runOnUiThread {
                                    Timber.d("filter :%s", s.toString())
                                    viewModel!!.searchWo(s.toString())
                                }
                            }
                        }, DELAY
                    )
                }
            })
        }
    }
}