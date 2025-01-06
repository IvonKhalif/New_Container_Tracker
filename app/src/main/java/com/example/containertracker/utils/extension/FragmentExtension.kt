package com.example.containertracker.utils.extension

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Created by yovi.putra on 23/07/22"
 * Project name: Container Tracker
 **/

/**
 * this function will safely show fragments
 * before show the fragment, this function will ensure
 * that the fragment to be displayed is not in the saved state
 */
fun showImmediately(
    fragmentManager: FragmentManager,
    tag: String,
    fragmentFactory: () -> DialogFragment
) {
    // find dialog to fragment manager, if not found so invoke to fragment factory
    val dialog = (fragmentManager.findFragmentByTag(tag) as? DialogFragment)
        ?: fragmentFactory.invoke()

    // has already been saved by its host
    if (!dialog.isStateSaved) {
        try {
            dialog.showNow(fragmentManager, tag)
        } catch (e: Throwable) {

        }
    }
}