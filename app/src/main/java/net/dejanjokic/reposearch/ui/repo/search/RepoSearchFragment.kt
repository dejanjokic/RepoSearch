package net.dejanjokic.reposearch.ui.repo.search

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import net.dejanjokic.reposearch.R
import net.dejanjokic.reposearch.core.Constants.APP.CURRENT_SEARCH_QUERY_KEY
import net.dejanjokic.reposearch.databinding.FragmentRepoSearchBinding
import net.dejanjokic.reposearch.util.ext.viewBinding

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class RepoSearchFragment : Fragment(R.layout.fragment_repo_search) {

    private val binding by viewBinding(FragmentRepoSearchBinding::bind)

    private val viewModel: RepoSearchViewModel by viewModels()

    private val adapter = RepoAdapter({showUserDetails(it)}, {showRepoDetails(it)})

    private var searchJob: Job? = null

    private var sortIndex = 0
    private var currentSearchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.sortIndex.observe(viewLifecycleOwner) {
            sortIndex = it
        }

        savedInstanceState?.let {
            currentSearchQuery = it.getString(CURRENT_SEARCH_QUERY_KEY, null)
            currentSearchQuery?.let { query ->
                search(query, false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initSearch()

        binding.buttonRetry.setOnClickListener { adapter.retry() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentSearchQuery?.let {
            outState.putString(CURRENT_SEARCH_QUERY_KEY, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_repo_search, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionSort -> {
                showSortDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initAdapter() = with(binding) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }
        recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header = RepoLoadStateAdapter { adapter.retry() },
            footer = RepoLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            progressBarRepoSearch.isVisible = loadState.source.refresh is LoadState.Loading
            buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
            textViewRepoSearchStatus.apply {
                isVisible = loadState.source.refresh is LoadState.Error
                text = (loadState.source.refresh as? LoadState.Error)?.error?.localizedMessage
            }

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                showStatusMessage(it.error.localizedMessage ?: "Error")
            }
        }
    }

    private fun initSearch() {
        binding.editTextSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
        binding.editTextSearchQuery.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.recyclerView.scrollToPosition(0) }
        }
    }

    private fun updateRepoListFromInput(shouldForceRefresh: Boolean = false) {
        binding.editTextSearchQuery.text?.trim().let {
            if (!it.isNullOrEmpty()) {
                currentSearchQuery = it.toString()
                search(it.toString(), shouldForceRefresh)
            }
        }
    }

    private fun search(query: String, shouldForceRefresh: Boolean) {
        val sort = resources.getStringArray(R.array.repo_sort_order)[sortIndex]
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchRepositories(query, sort, shouldForceRefresh).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun showSortDialog() {
        MaterialDialog(requireContext()).show {
            title(R.string.sort_by)
            listItemsSingleChoice(R.array.repo_sort_order_labels, initialSelection = sortIndex) { _, index, _ ->
                if (index != sortIndex) {
                    sortIndex = index
                    viewModel.saveSortIndexToPreferences(sortIndex)
                    updateRepoListFromInput(true)
                }
            }
        }
    }

    private fun showStatusMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showUserDetails(login: String) {
        val action = RepoSearchFragmentDirections.actionRepoSearchFragmentToUserDetailsFragment(login)
        findNavController().navigate(action)
    }

    private fun showRepoDetails(path: String) {
        val action = RepoSearchFragmentDirections.actionRepoSearchFragmentToRepoDetailsFragment(path)
        findNavController().navigate(action)
    }
}