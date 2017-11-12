package ca.allanwang.kau.searchview

import android.view.MenuItem

/**
 * Created by Allan Wang on 2017-11-12.
 *
 * Interface to help facilitate searchview binding and actions
 */
interface SearchViewHolder {

    var searchView: SearchView?

    fun searchViewBindIfNull(binder: () -> SearchView) {
        if (searchView == null) searchView = binder()
    }

    fun searchViewOnBackPress() = searchView?.onBackPressed() ?: false

    fun searchViewUnBind(replacementMenuItemClickListener: ((item: MenuItem) -> Boolean)? = null) {
        searchView?.unBind(replacementMenuItemClickListener)
        searchView = null
    }

}