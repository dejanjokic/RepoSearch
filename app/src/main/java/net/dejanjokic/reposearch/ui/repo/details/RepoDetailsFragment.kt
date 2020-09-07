package net.dejanjokic.reposearch.ui.repo.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.dejanjokic.reposearch.R
import net.dejanjokic.reposearch.data.model.repo.RepoDetailsResponse
import net.dejanjokic.reposearch.databinding.FragmentRepoDetailsBinding
import net.dejanjokic.reposearch.util.ext.setNonBlankText
import net.dejanjokic.reposearch.util.ext.viewBinding
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class RepoDetailsFragment : Fragment(R.layout.fragment_repo_details) {

    private val binding by viewBinding(FragmentRepoDetailsBinding::bind)

    private val viewModel: RepoDetailsViewModel by viewModels()

    private val args: RepoDetailsFragmentArgs by navArgs()

    private var htmlUrl: String? = null
    private var login: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val (login, name) = args.repoPath.split("/")
        viewModel.getRepoDetails(login, name)
        observeUiState()
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
                is RepoDetailsState.Success -> { bindRepoDetails(it.item) }
                is RepoDetailsState.Error -> { showErrorMessage(it.message) }
                is RepoDetailsState.Loading -> { showLoading() }
            }
        }
    }

    private fun bindRepoDetails(repo: RepoDetailsResponse) = with(binding) {
        login = repo.owner.login
        htmlUrl = repo.htmlUrl

        progressBarRepoDetails.isVisible = false
        textViewRepoDetailsStatus.isVisible = false
        contentRepoDetails.isVisible = true

        textViewRepoDetailsFullName.text = repo.fullName
        textViewRepoDetailsOwner.apply {
            text = repo.owner.login
            setOnClickListener {
                showUserDetails()
            }
        }

        val dateCreated = ZonedDateTime.parse(repo.createdAt).format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))
        textViewRepoDetailsCreatedDate.text = getString(R.string.date_created_template, dateCreated)
        val dateUpdated = ZonedDateTime.parse(repo.updatedAt).format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))
        textViewRepoDetailsUpdatedDate.text = getString(R.string.date_updated_template, dateUpdated)

        textViewRepoDetailsDefaultBranch.text = repo.defaultBranch
        textViewRepoDetailsLanguage.text = repo.language
        textViewRepoDetailsDescription.setNonBlankText(repo.description)

        textViewRepoDetailsStars.text = repo.stargazersCount.toString()
        textViewRepoDetailsForks.text = repo.forksCount.toString()
        textViewRepoDetailsWatchers.text = repo.watchersCount.toString()
        textViewRepoDetailsIssues.text = repo.openIssuesCount.toString()
    }

    private fun showErrorMessage(message: String) = with(binding) {
        contentRepoDetails.isVisible = false
        progressBarRepoDetails.isVisible = false
        textViewRepoDetailsStatus.apply {
            isVisible = true
            text = message
        }
    }

    private fun showLoading() = with(binding) {
        contentRepoDetails.isVisible = false
        textViewRepoDetailsStatus.isVisible = false
        progressBarRepoDetails.isVisible = true
    }

    private fun showUserDetails() {
        login?.let {
            val action = RepoDetailsFragmentDirections.actionRepoDetailsFragmentToUserDetailsFragment(it)
            findNavController().navigate(action)
        }
    }
}