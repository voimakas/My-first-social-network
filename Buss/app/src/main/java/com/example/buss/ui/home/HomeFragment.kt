package com.example.buss.ui.home

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buss.Model.Post
import com.example.buss.R
import com.example.buss.adapter.PostAdapter
import com.example.buss.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri: Uri? = null
    lateinit var listener : ValueEventListener
    private lateinit var adapter: PostAdapter
lateinit var postsRef : DatabaseReference

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null


    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {





        retrivePosts()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun retrivePosts() {
        postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

  listener = postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {

                for (snaphot in p0.children)
                {
                    val post = snaphot.getValue(Post::class.java)


                        if (post!!.getPublisher() == post.getPublisher())
                        {
postList!!.add(post)
                        }
                        postAdapter?.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var recyclerView: RecyclerView? = null
        recyclerView = view?.findViewById(R.id.recyclerView)
        var linearLayoutManger = LinearLayoutManager(context)
        linearLayoutManger.reverseLayout = true
        linearLayoutManger.stackFromEnd = true
        recyclerView?.layoutManager = linearLayoutManger
        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView?.adapter = postAdapter



    }

    override fun onStop() {
        super.onStop()
        if (postsRef != null && listener != null) {
postsRef.removeEventListener(listener)
       }
   }


}
