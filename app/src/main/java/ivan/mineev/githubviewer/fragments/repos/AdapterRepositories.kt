package ivan.mineev.githubviewer.fragments.repos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ivan.mineev.githubviewer.databinding.ItemRepoBinding
import ivan.mineev.githubviewer.model.Repo

class AdapterRepositories : RecyclerView.Adapter<AdapterRepositories.ViewHolder>() {

    var data: List<Repo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(private val binding: ItemRepoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: Repo) {
            binding.apply {

                privateImage.visibility = if (repo.isPrivate) View.VISIBLE else View.GONE
//                if (repo.isPrivate) privateImage.visibility = View.VISIBLE
//                else privateImage.visibility = View.GONE

                title.text = repo.name

                language.text = repo.language
                language.setTextColor(repo.color)

                if (repo.description.isEmpty()) description.visibility = View.GONE
                else {
                    description.visibility = View.VISIBLE
                    description.text = repo.description
                }

            }

        }
    }
}