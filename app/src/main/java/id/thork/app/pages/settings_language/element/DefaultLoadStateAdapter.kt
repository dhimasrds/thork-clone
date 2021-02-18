package id.thork.app.pages.settings_language.element

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R

class DefaultLoadStateAdapter : LoadStateAdapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, loadState: LoadState) = Unit

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.page_state_item, parent, false)
        ) {}
    }
}
