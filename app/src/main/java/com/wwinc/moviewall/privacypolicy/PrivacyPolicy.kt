package com.wwinc.moviewall.privacypolicy

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wwinc.moviewall.R
import com.wwinc.moviewall.databinding.FragmentPrivacyPolicyBinding


class PrivacyPolicy : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentPrivacyPolicyBinding>(inflater, R.layout.fragment_privacy_policy, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.textView6.justificationMode = JUSTIFICATION_MODE_INTER_WORD
        }

        return binding.root
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.random).setVisible(false)
        menu.findItem(R.id.app_bar_search).setVisible(false)
        menu.findItem(R.id.changeColumn).setVisible(false)
    }
}