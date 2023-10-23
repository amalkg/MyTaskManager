package com.cns.mytaskmanager.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

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

fun Fragment.showKeyboard(view: View) {
    lifecycleScope.launch {
        delay(50)
        val window = requireActivity().window
        val ime = WindowInsetsCompat.Type.ime()
        WindowCompat.getInsetsController(window, view).show(ime)
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

fun capitalizeFirstLetter(input: String): String {
    if (input.isEmpty()) {
        return input
    }
    return input.substring(0, 1).uppercase() + input.substring(1)
}

fun String.convertDateToMilliseconds(): Long {
    val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    val date = format.parse(this)
    return date?.time ?: 0
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}