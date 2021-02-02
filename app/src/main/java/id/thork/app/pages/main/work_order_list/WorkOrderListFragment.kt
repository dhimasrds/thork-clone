package id.thork.app.pages.main.work_order_list

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.databinding.FragmentWorkOrderListBinding
import id.thork.app.pages.main.element.WoLoadStateAdapter
import id.thork.app.pages.main.element.WorkOrderAdapter
import id.thork.app.pages.main.element.WorkOrderListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class WorkOrderListFragment : Fragment() {
    private lateinit var myAdapter: WorkOrderAdapter
    private val viewModel: WorkOrderListViewModel by viewModels()
    private lateinit var binding: FragmentWorkOrderListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorkOrderListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this@WorkOrderListFragment
        binding.viewModel = viewModel

        myAdapter = WorkOrderAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
//        progressBarOnFirstLoad()

//        viewLifecycleOwner.lifecycleScope.launch {
//
//            myAdapter.loadStateFlow.collectLatest { loadStates ->
//                Timber.d("onCreateView :%s", loadStates.refresh)
//                binding.progressBar.isVisible = loadStates.refresh is LoadState.Loading
//                binding.btnRetry.isVisible = loadStates.refresh !is LoadState.Loading
//                binding.btnRetry.setOnClickListener {
//                    setupObserver()
//                }
//            }
//        }
    }

    private fun setupView() {
        binding.recyclerView.apply {
            adapter = myAdapter.withLoadStateFooter(
                footer = WoLoadStateAdapter { myAdapter.retry() }
            )
        }

    }

    private fun setupObserver() {
//        viewModel.fetchWoList()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.woList.collect {
                myAdapter.submitData(it)
                Timber.d("onCreateView :%s", it)
            }
        }
    }

    private fun progressBarOnFirstLoad() {
        binding.apply {
            btnRetry.setOnClickListener {
                myAdapter.retry()
            }
            // show the loading state for te first load
            myAdapter.addLoadStateListener { loadState ->

                if (loadState.refresh is LoadState.Loading) {

                    btnRetry.visibility = View.GONE

                    // Show ProgressBar
                    progressBar.visibility = View.VISIBLE
                } else {
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
                        val toast = Toast.makeText(requireContext(), "it.error.message", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0 , 0)
                        toast.show()
                    }
                }
            }
        }
    }

}