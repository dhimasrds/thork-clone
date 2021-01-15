package id.thork.app.pages.main.work_order_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import id.thork.app.R
import id.thork.app.databinding.FragmentWorkOrderListBinding
import id.thork.app.pages.main.element.WorkOrderAdapter
import id.thork.app.pages.main.element.WorkOrderListViewModel
import id.thork.app.persistence.entity.WoEntity
import id.thork.app.utils.CustomDialogUtils
import timber.log.Timber

class WorkOrderListFragment : Fragment() {
//    private lateinit var binding: FragmentWorkOrderListBinding
    val arrayList = ArrayList<WoEntity>()
    private lateinit var myAdapter : WorkOrderAdapter

    private val viewModel : WorkOrderListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentWorkOrderListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


//        for (i in 1..20) {
//            val wo = WoEntity(
//                wonum = "125$i",
//                status = "Description $i",
//                laborCode = "Alfamart kota $i"
//            )
//            arrayList.add(wo)
//        }
//
//
//        Timber.d("list size :%s", arrayList.size)

        binding.apply {

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}