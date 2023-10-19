package com.cns.mytaskmanager.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController

fun Fragment.safeNavigate(
    directions: NavDirections,
    extras: Navigator.Extras? = null,
    @IdRes fromDest: Int? = null
) {
    val currDest = findNavController().currentDestination as? FragmentNavigator.Destination
    if (javaClass.name == currDest?.className || (fromDest != null && currDest?.id == fromDest)) {
        if (extras != null) {
            findNavController().navigate(directions, extras)
        } else {
            findNavController().navigate(directions)
        }
    }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.setCustomClickListener(l: (View) -> Unit) {
    setOnClickListener(CustomClickListener(l))
}

fun Fragment.hideKeyboard() {
    if (view != null) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val window = requireActivity().window
            val ime = WindowInsetsCompat.Type.ime()
            WindowCompat.getInsetsController(window, requireView()).hide(ime)
        }
    }
}
fun isNetworkAvailable(context: Context) =
    (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }