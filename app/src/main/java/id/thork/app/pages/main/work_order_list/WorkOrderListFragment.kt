package id.thork.app.pages.main.work_order_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.thork.app.R
import id.thork.app.databinding.FragmentWorkOrderListBinding
import id.thork.app.pages.main.element.WorkOrderAdapter
import id.thork.app.persistence.entity.WoEntity
import timber.log.Timber

class WorkOrderListFragment : Fragment() {
private lateinit var binding : FragmentWorkOrderListBinding
  val arrayList = ArrayList<WoEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorkOrderListBinding.inflate(inflater,container,false)

        for (i in 1..20){
            val wo = WoEntity(
                "random"
            )
            wo.wonum = "125$i"
            wo.status = "description $i"
            wo.laborCode = "alfamart kota $i"
            arrayList.add(wo)
        }
        val myAdapter = WorkOrderAdapter(arrayList)

        Timber.d("list size :%s", arrayList.size)

        binding.recyclerView.apply {
            binding.recyclerView.adapter = myAdapter
        }

        return binding.root
    }


}