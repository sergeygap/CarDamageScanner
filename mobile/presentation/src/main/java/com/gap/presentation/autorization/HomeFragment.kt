package com.gap.presentation.autorization

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gap.presentation.R

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveStartFragment()
    }

    private fun saveStartFragment() {
        val sharedPreferences = requireActivity()
            .getSharedPreferences(START_FRAGMENT_TAG, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(REACHED_HOME_FRAGMENT, true)
        editor.apply()
    }

    companion object {
        const val START_FRAGMENT_TAG = "start_fragment_tag"
        const val REACHED_HOME_FRAGMENT = "reachedHomeFragment"
    }
}