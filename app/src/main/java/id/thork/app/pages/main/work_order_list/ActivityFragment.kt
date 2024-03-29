package id.thork.app.pages.main.work_order_list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.work.WorkInfo
import com.baoyz.widget.PullRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.databinding.FragmentActivityBinding
import id.thork.app.pages.main.element.WoLoadStateAdapter
import id.thork.app.pages.main.element.WorkOrderActvityViewModel
import id.thork.app.pages.main.element.WorkOrderAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ActivityFragment : Fragment() {
    @Inject
    lateinit var woActivityAdapter: WorkOrderAdapter
    private lateinit var binding: FragmentActivityBinding
    private lateinit var pullRefreshLayout: PullRefreshLayout
    private val viewModel: WorkOrderActvityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentActivityBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        pullRefreshLayout = binding.swipeRefreshLayout
        viewModel.pruneWork()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
        setUpFilterListener()
        swipeRefresh()
        progressBarOnFirstLoad()
    }

    private fun setupView() {
        binding.recyclerView.apply {
            adapter = woActivityAdapter.withLoadStateFooter(
                footer = WoLoadStateAdapter { woActivityAdapter.retry() }
            )
        }
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.woList.observe(viewLifecycleOwner) {
                woActivityAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                Timber.d("onCreateView :%s", it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            woActivityAdapter.loadStateFlow.collectLatest { loadStates ->
                Timber.d("onCreateView loadstate :%s", loadStates)
                val mediatorPrependLoadState: LoadState? = loadStates.source.prepend
                Timber.d("onCreateView loa1dstate  mediator :%s", mediatorPrependLoadState!!.endOfPaginationReached)
                binding.progressBar.isVisible = loadStates.refresh is LoadState.Loading
//                binding.loadStateRetry.isVisible = loadStates.append is LoadState.Error
//               binding.loadStateErrorMessage.isVisible = loadStates.append is LoadState.Error
//                binding.loadStateErrorMessage.text = "Connection Lost, please try again
                if ( mediatorPrependLoadState!!.endOfPaginationReached && loadStates.append is LoadState.Error) {
                    val toast = Toast.makeText(
                        requireContext(),
                        "Server did not response, please try again",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.CENTER, 0, 50)
                    toast.show()
                }
            }
        }

        viewModel.outputWorkInfos.observe(viewLifecycleOwner, workInfosObserver())
    }

    private fun swipeRefresh() {
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL)
        pullRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.blueTextStatus),
            ContextCompat.getColor(requireContext(), R.color.colorYellow),
            ContextCompat.getColor(requireContext(), R.color.colorGreen)
        )

        pullRefreshLayout.setOnRefreshListener {
            woActivityAdapter.refresh()
            woActivityAdapter.addLoadStateListener { loadstate ->

                Timber.d("loadresult wo :%s", loadstate)
                if (loadstate.refresh is LoadState.Loading) {
                    binding.progressBar.visibility =View.VISIBLE
                    requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }else {
                    pullRefreshLayout.setRefreshing(false)
                    binding.progressBar.visibility =View.GONE
                    requireActivity().window .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                }
            }
        }
    }

    private fun progressBarOnFirstLoad() {
        binding.apply {
            btnRetry.setOnClickListener {
                woActivityAdapter.retry()
            }
            // show the loading state for te first load
            woActivityAdapter.addLoadStateListener { loadState ->

                if (loadState.refresh is LoadState.Loading) {

                    btnRetry.visibility = View.GONE

                    // Show ProgressBar
                    progressBar.visibility = View.VISIBLE

                }
                else {
                    // Hide ProgressBar
                    progressBar.visibility = View.GONE
                    Timber.d("progressBarOnFirstLoad :%s", loadState.append)
                    Timber.d("progressBarOnFirstLoad :%s", loadState.prepend)
                    Timber.d("progressBarOnFirstLoad :%s", loadState.refresh)
                    // If we have an error, show a toast
                    val errorState = when {
                        loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                        loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                        loadState.refresh is LoadState.Error -> {
                            Timber.d("btnretry :%s", "sukses")
                            btnRetry.visibility = View.VISIBLE
                            loadState.refresh as LoadState.Error
                        }

                        else -> null
                    }
                    errorState?.let {
//                        val toast = Toast.makeText(
//                            requireContext(),
//                            "Please,check your connection",
//                            Toast.LENGTH_LONG
//                        )
//                        toast.setGravity(Gravity.CENTER, 0, 0)
//                        toast.show()
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

    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listWorkInfo ->
            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (listWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            Timber.d("workInfosObserver() refresh adapter")
            woActivityAdapter.refresh()
            viewModel.pruneWork()

        }
    }
}