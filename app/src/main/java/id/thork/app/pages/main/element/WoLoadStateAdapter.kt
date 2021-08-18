package id.thork.app.pages.main.element

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.databinding.LoadStateViewBinding
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 22/01/21
 * Jakarta, Indonesia.
 */
class WoLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<WoLoadStateAdapter.LoadStateViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        LoadStateViewHolder(
            LoadStateViewBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.load_state_view, parent, false)
            )
        ) { retry.invoke() }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) =
        holder.bind(loadState)

    class LoadStateViewHolder(
        private val binding: LoadStateViewBinding,
        private val retryCallback: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.loadStateRetry.setOnClickListener { retryCallback() }
        }

        fun bind(loadState: LoadState) {
            Timber.d("loadstate error :%s", loadState)
            with(binding) {
                loadStateProgress.isVisible = loadState is LoadState.Loading
                loadStateRetry.isVisible = loadState is LoadState.Error
//                loadStateErrorMessage.isVisible = loadState is LoadState.Error
//                loadStateErrorMessage.text = "Connection Lost, please try again "
            }
        }
    }
}