package net.dejanjokic.reposearch.core

object Constants {

    object API {
        const val GITHUB_API_BASE_URL = "https://api.github.com/"
        const val GITHUB_SEARCH_STARTING_PAGE_INDEX = 1
        const val GITHUB_SEARCH_PAGE_SIZE = 50
    }

    object APP {
        const val PREFS_NAME = "github_prefs"
        const val PREFS_SORT_INDEX_KEY = "sort_index"
        const val CURRENT_SEARCH_QUERY_KEY = "current_query"
    }
}