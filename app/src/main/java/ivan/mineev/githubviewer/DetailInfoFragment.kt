package ivan.mineev.githubviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ivan.mineev.githubviewer.databinding.FragmentDetailInfoBinding

class DetailInfoFragment : Fragment() {

    private lateinit var binding: FragmentDetailInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}