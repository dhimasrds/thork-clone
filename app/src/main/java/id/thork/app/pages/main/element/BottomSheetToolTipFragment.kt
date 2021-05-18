package id.thork.app.pages.main.element

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.R
import id.thork.app.databinding.FragmentBottomSheetToolTipBinding

@AndroidEntryPoint
class BottomSheetToolTipFragment : RoundedBottomSheetDialogFragment() {
    private lateinit var binding :FragmentBottomSheetToolTipBinding
    private val viewModel : MapViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBottomSheetToolTipBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        return binding.root



    }

}