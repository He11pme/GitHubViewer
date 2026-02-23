package ivan.mineev.githubviewer.view.fragments.repos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ivan.mineev.githubviewer.databinding.ItemRepoBinding
import ivan.mineev.githubviewer.data.model.Repo

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
            binding.apply {

                privateImage.visibility = if (repo.isPrivate) View.VISIBLE else View.GONE

                title.text = repo.name

                language.text = repo.language
                language.setTextColor(repo.color)

                if (repo.description.isEmpty()) description.visibility = View.GONE
                else {
                    description.visibility = View.VISIBLE
                    description.text = repo.description
                }

            }

            binding.root.setOnClickListener { openRepoDetails(repo.name) }

        }
    }
}