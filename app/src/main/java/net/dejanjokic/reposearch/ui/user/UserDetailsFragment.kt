package net.dejanjokic.reposearch.ui.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.dejanjokic.reposearch.R
import net.dejanjokic.reposearch.data.model.repo.SimpleRepoResponse
import net.dejanjokic.reposearch.data.model.user.UserDetailsResponse
import net.dejanjokic.reposearch.databinding.FragmentUserDetailsBinding
import net.dejanjokic.reposearch.ui.repo.SimpleRepoAdapter
import net.dejanjokic.reposearch.util.ext.setNonBlankText
import net.dejanjokic.reposearch.util.ext.viewBinding
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class UserDetailsFragment : Fragment(R.layout.fragment_user_details) {

    private val binding by viewBinding(FragmentUserDetailsBinding::bind)

    private val viewModel: UserDetailsViewModel by viewModels()

    private val args: UserDetailsFragmentArgs by navArgs()

    private var htmlUrl: String? = null

    private val repoAdapter = SimpleRepoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getUserDetails(args.userLogin)
        observeUiState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = repoAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_web_link, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionOpenWebsite -> {
                htmlUrl?.let {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeUiState() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is UserDetailsState.Success -> {
                    it.user?.let { details -> bindUserDetails(details) }
                    it.repos?.let { repos -> bindUserRepos(repos) }
                }
                is UserDetailsState.Error -> { showErrorMessage(it.message) }
                is UserDetailsState.Loading -> { showLoading() }
            }
        }
    }

    private fun bindUserDetails(user: UserDetailsResponse) = with(binding) {
        htmlUrl = user.htmlUrl

        textViewUserDetailsStatus.isVisible = false
        progressBarUserDetails.isVisible = false
        contentUserDetails.isVisible = true

        textViewUserDetailsName.text = user.name ?: user.login

        textViewUserDetailsFollowings.text = getString(R.string.followers_following_template, user.followers, user.following)

        val dateJoined = ZonedDateTime.parse(user.createdAt).format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))
        textViewUserDetailsDateJoined.text = dateJoined

        textViewUserDetailsLocation.setNonBlankText(user.location)
        textViewUserDetailsCompany.setNonBlankText(user.company)
        textViewUserDetailsBlog.setNonBlankText(user.blog)
        textViewUserDetailsEmail.setNonBlankText(user.email)

        textViewUserDetailsRepoCount.text = getString(R.string.public_repos_template, user.publicRepos)

        imageViewUserDetailsAvatar.load(user.avatarUrl)
    }

    private fun bindUserRepos(repos: List<SimpleRepoResponse>) {
        repoAdapter.submitList(repos)
    }

    private fun showErrorMessage(message: String) = with(binding) {
        contentUserDetails.isVisible = false
        progressBarUserDetails.isVisible = false
        textViewUserDetailsStatus.apply {
            isVisible = true
            text = message
        }
    }

    private fun showLoading() = with(binding) {
        contentUserDetails.isVisible = false
        textViewUserDetailsStatus.isVisible = false
        progressBarUserDetails.isVisible = true
    }
}