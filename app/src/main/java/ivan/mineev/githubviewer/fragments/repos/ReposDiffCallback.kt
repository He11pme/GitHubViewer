package ivan.mineev.githubviewer.fragments.repos

import androidx.recyclerview.widget.DiffUtil
import ivan.mineev.githubviewer.model.Repo

class ReposDiffCallback : DiffUtil.ItemCallback<Repo>() {
    override fun areItemsTheSame(
        oldItem: Repo,
        newItem: Repo
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Repo,
        newItem: Repo
    ): Boolean {
        return oldItem == newItem
    }
}