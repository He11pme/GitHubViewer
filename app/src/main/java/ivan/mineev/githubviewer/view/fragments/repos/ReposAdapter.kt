package ivan.mineev.githubviewer.view.fragments.repos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ivan.mineev.githubviewer.databinding.ItemRepoBinding
import ivan.mineev.githubviewer.domain.model.Repo

class ReposAdapter(val openRepoDetails: (name: String) -> Unit) :
    ListAdapter<Repo, ReposAdapter.ViewHolder>(ReposDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemRepoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRepoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: Repo) {
            binding.repo = repo

            binding.root.setOnClickListener { openRepoDetails(repo.name) }

        }
    }
}