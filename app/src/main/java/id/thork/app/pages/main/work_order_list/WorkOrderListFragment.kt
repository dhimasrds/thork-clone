package id.thork.app.pages.main.work_order_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.databinding.FragmentWorkOrderListBinding
import id.thork.app.network.response.work_order.Member
import id.thork.app.pages.main.element.WorkOrderAdapter
import id.thork.app.pages.main.element.WorkOrderListViewModel
import id.thork.app.persistence.entity.WoEntity
import timber.log.Timber

@AndroidEntryPoint
class WorkOrderListFragment : Fragment() {
    private lateinit var myAdapter : WorkOrderAdapter

    private val viewModel : WorkOrderListViewModel by viewModels()
    private lateinit var binding : FragmentWorkOrderListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorkOrderListBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this@WorkOrderListFragment
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupView()
    }

    private fun setupView(){
        binding.apply {
            binding.recyclerView.adapter = myAdapter
        }
    }

    private fun setupObserver(){
        myAdapter = WorkOrderAdapter()
        viewModel.fetchWoList()
        viewModel.getWoList.observe(viewLifecycleOwner){
            myAdapter.submitList(it)
            Timber.d("onCreateView :%s", it.size)
        }
    }

}