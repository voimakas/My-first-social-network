package com.example.buss.ui.stories

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.buss.Model.User

import com.example.buss.databinding.FragmentStoriesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class StoriesFragment : Fragment() {

    private var _binding: FragmentStoriesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(StoriesViewModel::class.java)

        _binding = FragmentStoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}