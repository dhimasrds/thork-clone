package id.thork.app.pages.main.element

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.databinding.LoadStateViewBinding
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 22/01/21
 * Jakarta, Indonesia.
 */
class WoLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<WoLoadStateAdapter.LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {

        val progress = holder.loadStateViewBinding.loadStateProgress
        val btnRetry = holder.loadStateViewBinding.loadStateRetry
        val txtErrorMessage = holder.loadStateViewBinding.loadStateErrorMessage

        Timber.d("ProgressBar :%s", loadState.toString())

        btnRetry.isVisible = loadState !is LoadState.Loading
        txtErrorMessage.isVisible = loadState !is LoadState.Loading
        progress.isVisible = loadState is LoadState.Loading

        if (loadState is LoadState.Error) {
            txtErrorMessage.text = "Lost connection or check your connection"
        }
        btnRetry.setOnClickListener {
            retry.invoke()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(
            LoadStateViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class LoadStateViewHolder(val loadStateViewBinding: LoadStateViewBinding) :
        RecyclerView.ViewHolder(loadStateViewBinding.root)
}