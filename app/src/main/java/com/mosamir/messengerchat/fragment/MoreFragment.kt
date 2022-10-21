package com.mosamir.messengerchat.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mosamir.messengerchat.R

class MoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val tv_title = activity?.findViewById<TextView>(R.id.tv_toolbar_title)
        tv_title?.text = "Discover"

        return inflater.inflate(R.layout.fragment_more, container, false)
    }

}